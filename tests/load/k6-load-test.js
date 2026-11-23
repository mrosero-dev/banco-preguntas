
import http from 'k6/http';
import { check, sleep } from 'k6';

const KEYCLOAK = __ENV.KEYCLOAK_URL || 'http://localhost:8180';
const QUESTION = __ENV.QUESTION_URL || 'http://localhost:8080';
const VUS = Number(__ENV.VUS || 5);

export const options = {
    stages: [
        { duration: '10s', target: VUS },
        { duration: '15s', target: VUS },
        { duration: '5s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.01'],
    },
};

export function setup() {
    const response = http.post(
        `${KEYCLOAK}/realms/banco-preguntas/protocol/openid-connect/token`,
        {
            grant_type: 'password',
            client_id: 'banco-preguntas-client',
            username: 'autor1@unicauca.edu.co',
            password: 'autor123',
        }
    );

    const validToken = check(response, {
        'token obtenido': (r) => r.status === 200,
    });

    if (!validToken) {
        throw new Error(`No se pudo obtener el token. HTTP ${response.status}: ${response.body}`);
    }

    return { token: response.json('access_token') };
}

function question(number) {
    return JSON.stringify({
        authorId: 2,
        statement: `Load test question ${number}`,
        context: 'Generated question for performance testing',
        difficulty: 'FACIL',
        category: 'MATEMATICAS',
        topic: 'ARITMETICA',
        subtopic: 'SUMA',
        competence: 'RAZONAMIENTO',
        justification: 'Validation of question creation through API',
        source: 'K6 performance test',
        options: [
            { text: 'Option A', correct: true },
            { text: 'Option B', correct: false },
            { text: 'Option C', correct: false },
            { text: 'Option D', correct: false },
            { text: 'Option E', correct: false },
        ],
    });
}

export default function (data) {
    const headers = {
        Authorization: `Bearer ${data.token}`,
        'Content-Type': 'application/json; charset=utf-8',
    };

    const number = `${__VU}-${__ITER}`;

    const created = http.post(
        `${QUESTION}/api/questions`,
        question(number),
        { headers }
    );

    const createdOk = check(created, {
        'pregunta creada (201)': (r) => r.status === 201,
    });

    const list = http.get(
        `${QUESTION}/api/questions?authorId=2&page=0&size=10`,
        { headers }
    );

    check(list, {
        'listado (200)': (r) => r.status === 200,
    });

    if (!createdOk) {
        console.error(`Error al crear pregunta: HTTP ${created.status} - ${created.body}`);
    }

    sleep(1);
}

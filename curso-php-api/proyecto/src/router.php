<?php

declare(strict_types=1);

function route(string $method, string $path, PDO $db): void
{
    if ($method === 'GET' && $path === '/health') {
        json_response([
            'ok' => true,
            'time' => gmdate('c'),
        ]);
        return;
    }

    if ($path === '/todos') {
        if ($method === 'GET') {
            json_response(['data' => todos_list($db)]);
            return;
        }
        if ($method === 'POST') {
            $body = request_json_body();
            if ($body === null) {
                abort_json(400, 'Missing JSON body');
            }
            json_response(['data' => todos_create($db, $body)], 201);
            return;
        }
        abort_json(405, 'Method Not Allowed');
    }

    if (preg_match('#^/todos/(\\d+)$#', $path, $m) === 1) {
        $id = (int) $m[1];
        if ($method === 'GET') {
            json_response(['data' => todos_get($db, $id)]);
            return;
        }
        if ($method === 'PATCH') {
            $body = request_json_body();
            if ($body === null) {
                abort_json(400, 'Missing JSON body');
            }
            json_response(['data' => todos_patch($db, $id, $body)]);
            return;
        }
        if ($method === 'DELETE') {
            todos_delete($db, $id);
            http_response_code(204);
            return;
        }
        abort_json(405, 'Method Not Allowed');
    }

    abort_json(404, 'Not Found');
}


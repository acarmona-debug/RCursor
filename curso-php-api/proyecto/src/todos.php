<?php

declare(strict_types=1);

function str_len(string $s): int
{
    if (function_exists('mb_strlen')) {
        return mb_strlen($s, 'UTF-8');
    }
    return strlen($s);
}

/**
 * @return array<string, mixed>
 */
function todo_row_to_api(array $row): array
{
    return [
        'id' => (int) $row['id'],
        'title' => (string) $row['title'],
        'done' => ((int) $row['done']) === 1,
        'created_at' => (string) $row['created_at'],
        'updated_at' => (string) $row['updated_at'],
    ];
}

/**
 * @return list<array<string, mixed>>
 */
function todos_list(PDO $db): array
{
    $stmt = $db->query('SELECT id, title, done, created_at, updated_at FROM todos ORDER BY id DESC');
    $rows = $stmt->fetchAll();

    $out = [];
    foreach ($rows as $row) {
        if (is_array($row)) {
            $out[] = todo_row_to_api($row);
        }
    }
    return $out;
}

/**
 * @return array<string, mixed>
 */
function todos_get(PDO $db, int $id): array
{
    $stmt = $db->prepare('SELECT id, title, done, created_at, updated_at FROM todos WHERE id = :id');
    $stmt->execute([':id' => $id]);
    $row = $stmt->fetch();
    if (!is_array($row)) {
        abort_json(404, 'Todo not found');
    }
    return todo_row_to_api($row);
}

/**
 * @param array<string, mixed> $body
 * @return array<string, mixed>
 */
function todos_create(PDO $db, array $body): array
{
    $errors = [];

    $title = $body['title'] ?? null;
    if (!is_string($title) || trim($title) === '') {
        $errors[] = 'title must be a non-empty string';
    } else {
        $title = trim($title);
        if (str_len($title) > 200) {
            $errors[] = 'title must be at most 200 characters';
        }
    }

    $done = $body['done'] ?? false;
    if (!is_bool($done)) {
        $errors[] = 'done must be boolean';
    }

    if ($errors !== []) {
        abort_json(422, 'Validation error', ['errors' => $errors]);
    }

    $stmt = $db->prepare('INSERT INTO todos (title, done) VALUES (:title, :done)');
    $stmt->execute([
        ':title' => $title,
        ':done' => $done ? 1 : 0,
    ]);

    $id = (int) $db->lastInsertId();
    return todos_get($db, $id);
}

/**
 * @param array<string, mixed> $body
 * @return array<string, mixed>
 */
function todos_patch(PDO $db, int $id, array $body): array
{
    $errors = [];
    $fields = [];
    $params = [':id' => $id];

    if (array_key_exists('title', $body)) {
        $title = $body['title'];
        if (!is_string($title) || trim($title) === '') {
            $errors[] = 'title must be a non-empty string';
        } else {
            $title = trim($title);
            if (str_len($title) > 200) {
                $errors[] = 'title must be at most 200 characters';
            } else {
                $fields[] = 'title = :title';
                $params[':title'] = $title;
            }
        }
    }

    if (array_key_exists('done', $body)) {
        $done = $body['done'];
        if (!is_bool($done)) {
            $errors[] = 'done must be boolean';
        } else {
            $fields[] = 'done = :done';
            $params[':done'] = $done ? 1 : 0;
        }
    }

    if ($fields === []) {
        $errors[] = 'at least one of: title, done';
    }

    if ($errors !== []) {
        abort_json(422, 'Validation error', ['errors' => $errors]);
    }

    // Ensure it exists first, for a clean 404.
    todos_get($db, $id);

    $sql = 'UPDATE todos SET ' . implode(', ', $fields) . ' WHERE id = :id';
    $stmt = $db->prepare($sql);
    $stmt->execute($params);

    return todos_get($db, $id);
}

function todos_delete(PDO $db, int $id): void
{
    // Ensure it exists first, for a clean 404.
    todos_get($db, $id);

    $stmt = $db->prepare('DELETE FROM todos WHERE id = :id');
    $stmt->execute([':id' => $id]);
}


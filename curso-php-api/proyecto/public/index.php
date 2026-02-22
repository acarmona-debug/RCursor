<?php

declare(strict_types=1);

require __DIR__ . '/../src/response.php';
require __DIR__ . '/../src/request.php';
require __DIR__ . '/../src/db.php';
require __DIR__ . '/../src/todos.php';
require __DIR__ . '/../src/router.php';

set_error_handler(function (int $severity, string $message, string $file, int $line): bool {
    if (!(error_reporting() & $severity)) {
        return false;
    }
    throw new ErrorException($message, 0, $severity, $file, $line);
});

try {
    $db = db();
    $method = request_method();
    $path = request_path();
    route($method, $path, $db);
} catch (HttpException $e) {
    json_error($e->status, $e->getMessage(), $e->details);
} catch (Throwable $e) {
    json_error(500, 'Internal Server Error', [
        'type' => get_class($e),
        'message' => $e->getMessage(),
    ]);
}


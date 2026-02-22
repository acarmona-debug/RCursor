<?php

declare(strict_types=1);

/**
 * @return non-empty-string
 */
function request_method(): string
{
    $m = $_SERVER['REQUEST_METHOD'] ?? 'GET';
    if (!is_string($m) || $m === '') {
        return 'GET';
    }
    return strtoupper($m);
}

/**
 * @return string
 */
function request_path(): string
{
    $uri = $_SERVER['REQUEST_URI'] ?? '/';
    if (!is_string($uri) || $uri === '') {
        return '/';
    }
    $path = parse_url($uri, PHP_URL_PATH);
    if (!is_string($path) || $path === '') {
        return '/';
    }
    return $path;
}

/**
 * @return array<string, string>
 */
function request_query(): array
{
    $out = [];
    foreach ($_GET as $k => $v) {
        if (!is_string($k)) {
            continue;
        }
        if (is_string($v)) {
            $out[$k] = $v;
        }
    }
    return $out;
}

function request_raw_body(): string
{
    $raw = file_get_contents('php://input');
    return is_string($raw) ? $raw : '';
}

/**
 * @return array<string, mixed>|null
 */
function request_json_body(): ?array
{
    $raw = request_raw_body();
    if (trim($raw) === '') {
        return null;
    }

    $data = json_decode($raw, true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        abort_json(400, 'Invalid JSON', [
            'json_error' => json_last_error_msg(),
        ]);
    }
    if (!is_array($data)) {
        abort_json(400, 'JSON body must be an object');
    }
    /** @var array<string, mixed> $data */
    return $data;
}


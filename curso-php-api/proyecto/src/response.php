<?php

declare(strict_types=1);

final class HttpException extends RuntimeException
{
    public int $status;
    /** @var array<string, mixed>|null */
    public ?array $details;

    /**
     * @param array<string, mixed>|null $details
     */
    public function __construct(int $status, string $message, ?array $details = null)
    {
        parent::__construct($message);
        $this->status = $status;
        $this->details = $details;
    }
}

/**
 * @param mixed $data
 * @param array<string, string> $headers
 */
function json_response(mixed $data, int $status = 200, array $headers = []): void
{
    http_response_code($status);
    header('Content-Type: application/json; charset=utf-8');
    foreach ($headers as $k => $v) {
        header($k . ': ' . $v);
    }

    echo json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}

/**
 * @param array<string, mixed>|null $details
 */
function json_error(int $status, string $message, ?array $details = null): void
{
    $payload = [
        'error' => [
            'message' => $message,
            'status' => $status,
        ],
    ];
    if ($details !== null) {
        $payload['error']['details'] = $details;
    }
    json_response($payload, $status);
}

/**
 * @param array<string, mixed>|null $details
 */
function abort_json(int $status, string $message, ?array $details = null): never
{
    throw new HttpException($status, $message, $details);
}

function no_content(int $status = 204): void
{
    http_response_code($status);
    header('Content-Type: application/json; charset=utf-8');
}


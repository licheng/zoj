<?php

function sandbox_shutdown_function() {
    $error = error_get_last();
    if ($error['type'] == 1 && substr($error['message'], 0, 19) == 'Allowed memory size') {
        posix_kill(posix_getpid(), SIGKILL);
        exit(1);
    }
}

register_shutdown_function("sandbox_shutdown_function");
posix_uname();
require 'p.php';


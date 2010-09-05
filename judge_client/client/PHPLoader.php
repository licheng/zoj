<?php

function sandbox_shutdown_function() {
    $error = error_get_last();
    if ($error['type'] == 1) {
        if (strpos($error['message'], 'Allowed memory size') === 0) {
            posix_kill(posix_getpid(), SIGKILL);
        }
    }
}

function sandbox_error_function($errno, $errstr, $errfile, $errline) {
    if (strpos($errstr, 'Division by zero') === 0) {
        posix_kill(posix_getpid(), SIGFPE);
    }
}

register_shutdown_function("sandbox_shutdown_function");
set_error_handler("sandbox_error_function");

# special signal send to judge notifing program is ready to run
posix_uname();

require 'p.php';


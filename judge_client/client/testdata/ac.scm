(define gao
  (lambda (a b)
    (if (eof-object? a)
      0
      (begin
        (display (+ a b))
        (newline)
        (gao (read) (read))
        )
      )
    )
  )

(gao (read) (read))


(define gao
  (lambda (a)
    (gao (+ a 1))
    )
  )

(gao 1)


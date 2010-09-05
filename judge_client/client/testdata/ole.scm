(define gao
  (lambda (a)
    (begin
      (display a)
      (gao a)
      )
    )
  )

(gao "11111111111111111111111111111111111111111111111111111")


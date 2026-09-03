import { forwardRef, type InputHTMLAttributes } from 'react'
import { cn } from '@/lib/cn'
import { inputBaseClasses, stateClasses } from './inputStyles'

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  invalid?: boolean
}

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  { invalid, className, ...rest },
  ref,
) {
  return (
    <input
      ref={ref}
      aria-invalid={invalid || undefined}
      className={cn(inputBaseClasses, stateClasses(invalid), className)}
      {...rest}
    />
  )
})

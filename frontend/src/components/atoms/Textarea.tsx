import { forwardRef, type TextareaHTMLAttributes } from 'react'
import { cn } from '@/lib/cn'
import { inputBaseClasses, stateClasses } from './inputStyles'

export interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  invalid?: boolean
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(function Textarea(
  { invalid, className, rows = 3, ...rest },
  ref,
) {
  return (
    <textarea
      ref={ref}
      rows={rows}
      aria-invalid={invalid || undefined}
      className={cn(inputBaseClasses, stateClasses(invalid), 'resize-y', className)}
      {...rest}
    />
  )
})

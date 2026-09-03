/** Shared classes for text-like form controls (Input, Select, Textarea). */
export const inputBaseClasses =
  'block w-full rounded-xl border bg-white px-3.5 py-2.5 text-sm text-gray-900 placeholder:text-gray-400 ' +
  'transition-colors focus:outline-none focus:ring-2 disabled:cursor-not-allowed disabled:bg-gray-100 disabled:text-gray-500'

export function stateClasses(invalid: boolean | undefined): string {
  return invalid
    ? 'border-red-400 focus:border-red-500 focus:ring-red-200'
    : 'border-gray-300 focus:border-emerald-500 focus:ring-emerald-200'
}

import { Spinner } from '@/components/atoms/Spinner'
import { Alert } from '@/components/molecules/Alert'
import { useFileUrl } from '@/lib/files'

export interface FilePreviewProps {
  fileId: string | null | undefined
  alt: string
  className?: string
}

/** Renders a stored file inline: images as `<img>`, PDFs in an `<iframe>`. */
export function FilePreview({ fileId, alt, className }: FilePreviewProps) {
  const { url, contentType, loading, error } = useFileUrl(fileId)

  if (!fileId) return <p className="text-sm text-gray-500">Sin archivo.</p>
  if (loading) {
    return (
      <div className="flex justify-center py-8">
        <Spinner />
      </div>
    )
  }
  if (error || !url) return <Alert kind="error">{error ?? 'No fue posible cargar el archivo.'}</Alert>
  if (contentType?.includes('pdf')) {
    return <iframe src={url} title={alt} className={className ?? 'h-[70vh] w-full rounded-xl border border-gray-200'} />
  }
  return <img src={url} alt={alt} className={className ?? 'max-h-[70vh] w-full rounded-xl object-contain'} />
}

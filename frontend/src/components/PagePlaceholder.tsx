interface PagePlaceholderProps {
  title: string
  description: string
  phase: string
}

// Used by pages whose real content is built in a later phase.
export default function PagePlaceholder({ title, description, phase }: PagePlaceholderProps) {
  return (
    <section>
      <h1 className="text-2xl font-bold">{title}</h1>
      <p className="mt-2 text-slate-600">{description}</p>
      <div className="mt-6 rounded-lg border border-dashed border-slate-300 bg-white p-8 text-center text-slate-500">
        This screen is built in {phase}.
      </div>
    </section>
  )
}

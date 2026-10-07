export interface BarDatum {
  label: string
  value: number
  color: string
}

/** A small horizontal bar chart made of plain HTML (no chart library needed). */
export default function BarChart({ title, data }: { title: string; data: BarDatum[] }) {
  const max = Math.max(1, ...data.map((item) => item.value))
  return (
    <figure className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <figcaption className="font-semibold">{title}</figcaption>
      <ul className="mt-4 space-y-3">
        {data.map((item) => (
          <li key={item.label}>
            <div className="mb-1 flex justify-between text-sm">
              <span>{item.label}</span>
              <span className="font-semibold">{item.value}</span>
            </div>
            <div className="h-3 rounded-full bg-slate-100" role="presentation">
              <div className="h-3 rounded-full" style={{ width: `${(item.value / max) * 100}%`, backgroundColor: item.color }} />
            </div>
          </li>
        ))}
      </ul>
    </figure>
  )
}

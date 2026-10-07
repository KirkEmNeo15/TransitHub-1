import { colorForType } from '../utils/transportColors'

/** The transportation type with its map color, for example a blue dot and "Bus". */
export default function TypeBadge({ type }: { type: string }) {
  return (
    <span className="inline-flex items-center gap-1.5 rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium">
      <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: colorForType(type) }} />
      {type}
    </span>
  )
}

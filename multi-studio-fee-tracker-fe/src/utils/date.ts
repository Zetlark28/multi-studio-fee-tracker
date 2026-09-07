export function today(): string {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function currentMonth(): string {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  return `${year}-${month}`;
}

/** Encodes an <input type="date"> value (YYYY-MM-DD) as ddmmyyyy, e.g. 2026-09-01 -> 1092026. */
export function encodeDate(isoDate: string): number {
  const [year, month, day] = isoDate.split('-').map(Number);
  return day * 1_000_000 + month * 10_000 + year;
}

/** Decodes a ddmmyyyy encoded date back to a DD/MM/YYYY display string. */
export function decodeDate(dateCode: number): string {
  const day = Math.floor(dateCode / 1_000_000);
  const month = Math.floor((dateCode % 1_000_000) / 10_000);
  const year = dateCode % 10_000;
  return `${String(day).padStart(2, '0')}/${String(month).padStart(2, '0')}/${year}`;
}

/** Encodes an <input type="month"> value (YYYY-MM) as mmyyyy, e.g. 2026-09 -> 92026. */
export function encodeMonth(isoMonth: string): number {
  const [year, month] = isoMonth.split('-').map(Number);
  return month * 10_000 + year;
}

/** Builds a ddmmyyyy encoded date from its numeric parts. */
export function buildDateCode(day: number, month: number, year: number): number {
  return day * 1_000_000 + month * 10_000 + year;
}

/** Number of days in the given month (1-12) of the given year. */
export function daysInMonth(month: number, year: number): number {
  return new Date(year, month, 0).getDate();
}

/** Day of week (0 = Monday .. 6 = Sunday) of the 1st of the given month. */
export function firstWeekdayOfMonth(month: number, year: number): number {
  return (new Date(year, month - 1, 1).getDay() + 6) % 7;
}

const MONTH_NAMES = [
  'gennaio',
  'febbraio',
  'marzo',
  'aprile',
  'maggio',
  'giugno',
  'luglio',
  'agosto',
  'settembre',
  'ottobre',
  'novembre',
  'dicembre',
];

/** Decodes a mmyyyy encoded month back to a display label, e.g. 92026 -> "settembre 2026". */
export function decodeMonthLabel(monthCode: number): string {
  const month = Math.floor(monthCode / 10_000);
  const year = monthCode % 10_000;
  return `${MONTH_NAMES[month - 1] ?? month} ${year}`;
}

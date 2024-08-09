export function dateToString(date: Date | undefined): string | undefined {
  if (date == undefined) {
    return undefined;
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-based in JavaScript, so add 1
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}

export interface FileHeader {
  id: string;
  folder: string;
  filename: string;
  thumbnailName: string;
  thumbnail: string | undefined;
  description: string;
  version: string;
  status: string;
  date: string;
  type: string;
  size: number;
  printableSize: string;
  keywords: string[];
}

export function convertSizeToPrintable(size: number): string {
  if (size < 1024) {
    return size + ' bytes';
  }
  if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB';
  }
  return (size / 1024 / 1024).toFixed(2) + ' MB';
}

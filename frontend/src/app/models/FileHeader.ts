export interface FileHeader {
    id: string;
    filename: string;
    thumbnailName: string;
    thumbnail: string | undefined;
    description: string;
    version: string;
    status: string;
    date: string;
    type: string;
    size: number;
    keywords: string[];
}
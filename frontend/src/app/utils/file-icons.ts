import { MimeTypes } from "./mime-types";

export function getIconFor(type: string): string {

    type = MimeTypes.extension(type) || type;

    const path = 'icons/filetypes/';

    switch (type) {
        case 'ai':
            return path + 'ai.svg';
        case 'doc':
        case 'docx':
            return path + 'docx.svg';
        case 'xls':
        case 'xlsx':
            return path + 'xls.svg';
        case 'ppt':
        case 'pptx':
            return path + 'ppt.svg';
        case 'pdf':
            return path + 'pdf.svg';
        case 'zip':
        case 'rar':
        case '7z':
            return path + 'zip.svg';
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif':
        case 'bmp':
        case 'svg':
            return path + 'image.svg';
        case 'mp4':
        case 'avi':
        case 'mkv':
        case 'mov':
            return path + 'videos.svg';
        case 'indd':
            return path + 'indd.svg';
        case 'psd':
            return path + 'psd.svg';
        default:
            return path + 'file.svg';
    }
}

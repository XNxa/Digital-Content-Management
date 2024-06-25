/*!
 * mime-types
 * Copyright(c) 2014 Jonathan Ong <me@jongleberry.com>
 * Copyright(c) 2015 Douglas Christopher Wilson <doug@somethingdoug.com>
 * MIT Licensed
 * 
 * This file has been modified from its original version.
 * The original code has been adapted to work in an Angular environment and encapsulated into a class.
 */

/**
 * The MIT License (MIT)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


import mimeDb from 'mime-db';

function extname(path: string): string {
  const lastIndex = path.lastIndexOf('.');
  return lastIndex !== -1 ? path.slice(lastIndex) : '';
}

const EXTRACT_TYPE_REGEXP = /^\s*([^;\s]*)(?:;|\s|$)/;
const TEXT_TYPE_REGEXP = /^text\//i;

/**
 * Represents a utility class for working with MIME types.
 */
export class MimeTypes {
  private static extensions: { [key: string]: string[] } = {};
  private static types: { [key: string]: string } = {};

  static {
    this.populateMaps(this.extensions, this.types);
  }

  /**
   * Returns the charset for the given MIME type.
   * @param type - The MIME type.
   * @returns The charset for the given MIME type, or `false` if the type is invalid or no charset is found.
   */
  static charset(type: string): string | false {
    if (!type || typeof type !== 'string') {
      return false;
    }

    const match = EXTRACT_TYPE_REGEXP.exec(type);
    const mime = match && mimeDb[match[1].toLowerCase()];

    if (mime && mime.charset) {
      return mime.charset;
    }

    if (match && TEXT_TYPE_REGEXP.test(match[1])) {
      return 'UTF-8';
    }

    return false;
  }

  
  /**
   * Returns the content type based on the provided string.
   * If the string is not a valid content type, it returns false.
   * @param str - The string to determine the content type from.
   * @returns The content type string or false if the string is not a valid content type.
   */
  static contentType(str: string): string | false {
    if (!str || typeof str !== 'string') {
      return false;
    }

    const mime = str.indexOf('/') === -1 ? this.lookup(str) : str;

    if (!mime) {
      return false;
    }

    if (mime.indexOf('charset') === -1) {
      const charset = this.charset(mime);
      if (charset) {
        return mime + '; charset=' + charset.toLowerCase();
      }
    }

    return mime;
  }

  /**
   * Returns the file extension for the given MIME type.
   * @param type - The MIME type.
   * @returns The file extension corresponding to the MIME type, or `false` if the type is invalid or no extension is found.
   */
  static extension(type: string): string | false {
    if (!type || typeof type !== 'string') {
      return false;
    }

    const match = EXTRACT_TYPE_REGEXP.exec(type);
    const exts = match && this.extensions[match[1].toLowerCase()];

    if (!exts || !exts.length) {
      return false;
    }

    return exts[0];
  }

  
  /**
   * Looks up the MIME type based on the file extension of the given path.
   * @param path - The file path.
   * @returns The MIME type associated with the file extension, or `false` if the path is invalid or the extension is not found.
   */
  static lookup(path: string): string | false {
    if (!path || typeof path !== 'string') {
      return false;
    }

    const extension = extname('x.' + path).toLowerCase().slice(1);

    if (!extension) {
      return false;
    }

    return this.types[extension] || false;
  }

  private static populateMaps(extensions: { [key: string]: string[] }, types: { [key: string]: string }) {
    const preference = ['nginx', 'apache', undefined, 'iana'];

    Object.keys(mimeDb).forEach((type) => {
      const mime = mimeDb[type];
      const exts = mime.extensions;

      if (!exts || !exts.length) {
        return;
      }

      extensions[type] = exts.slice();

      for (let i = 0; i < exts.length; i++) {
        const extension = exts[i];

        if (types[extension]) {
          const from = preference.indexOf(mimeDb[types[extension]].source);
          const to = preference.indexOf(mime.source);

          if (
            types[extension] !== 'application/octet-stream' &&
            (from > to || (from === to && types[extension].slice(0, 12) === 'application/'))
          ) {
            continue;
          }
        }

        types[extension] = type;
      }
    });
  }
}

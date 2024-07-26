import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThumbnailCacheService {
  private cache = new Map<string, string>();

  /**
   * Generate a unique key for the cache.
   * @param folder
   * @param filename
   * @returns a unique key
   */
  private generateKey(folder: string, filename: string): string {
    return `${folder}/${filename}`;
  }

  /**
   * Try to get an image from the cache. If it is not present, return null.
   * @param folder
   * @param filename
   * @returns the URL to the cached image or null
   */
  get(folder: string, filename: string): string | null {
    const key = this.generateKey(folder, filename);
    if (this.cache.has(key)) {
      return this.cache.get(key)!;
    } else {
      return null;
    }
  }

  /**
   * Takes an image and creates a URL. Returns the URL to the cached image.
   * @param folder
   * @param filename
   * @param image the blob containing the image
   * @returns the URL to the cached image
   */
  set(folder: string, filename: string, image: Blob): string {
    const key = this.generateKey(folder, filename);
    if (this.cache.has(key)) {
      this.remove(folder, filename);
    }
    const url = URL.createObjectURL(image);
    this.cache.set(key, url);
    return url;
  }

  /**
   * Remove an image from the cache.
   * @param folder
   * @param filename
   */
  remove(folder: string, filename: string): void {
    const key = this.generateKey(folder, filename);
    if (!this.cache.has(key)) return;
    URL.revokeObjectURL(this.cache.get(key)!);
    this.cache.delete(key);
  }
}

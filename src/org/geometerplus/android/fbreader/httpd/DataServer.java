/*
 * Copyright (C) 2009-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.httpd;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLFileImageProxy;
import org.geometerplus.zlibrary.core.image.ZLImage;
import org.geometerplus.zlibrary.core.util.MimeType;
import org.geometerplus.zlibrary.core.util.SliceInputStream;

import org.geometerplus.fbreader.book.BookUtil;

public class DataServer extends NanoHTTPD {
	DataServer(int port) {
		super(port);
	}

	@Override
	public Response serve(String uri, Method method, Map<String,String> headers, Map<String,String> params, Map<String,String> files) {
		if (uri.startsWith("/cover/")) {
			return serveCover(uri, method, headers, params, files);
		} else if (uri.startsWith("/video")) {
			return serveVideo(uri, method, headers, params, files);
		} else {
			return notFound(uri);
		}
	}

	private Response serveCover(String uri, Method method, Map<String,String> headers, Map<String,String> params, Map<String,String> files) {
		try {
			final ZLImage image = BookUtil.getCover(DataUtil.fileFromEncodedPath(uri.substring(7)));
			if (image instanceof ZLFileImageProxy) {
				final ZLFileImageProxy proxy = (ZLFileImageProxy)image;
				proxy.synchronize();
				final InputStream stream = proxy.getRealImage().inputStream();
				if (stream == null) {
					return notFound(uri);
				}
				return new Response(Response.Status.OK, MimeType.IMAGE_PNG.toString(), stream);
			} else /* TODO: process PluginImage & null */ {
				return notFound(uri);
			}
		} catch (Exception e) {
			return forbidden(uri, e);
		}
	}

	private Response serveVideo(String uri, Method method, Map<String,String> headers, Map<String,String> params, Map<String,String> files) {
		String mime = null;
		for (MimeType mimeType : MimeType.TYPES_VIDEO) {
			final String m = mimeType.toString();
			if (uri.startsWith("/" + m + "/")) {
				mime = m;
				break;
			}
		}
		if (mime == null) {
			return notFound(uri);
		}
		try {
			return serveFile(DataUtil.fileFromEncodedPath(uri.substring(mime.length() + 2)), mime, headers);
		} catch (Exception e) {
			return forbidden(uri, e);
		}
	}

	private static final String BYTES_PREFIX = "bytes=";

	private Response serveFile(ZLFile file, String mime, Map<String,String> headers) throws IOException {
		final Response res;
		final InputStream baseStream = file.getInputStream();
		final int fileLength = baseStream.available();
		final String etag = '"' + Integer.toHexString(file.getPath().hashCode()) + '"';

		final String range = headers.get("range");
		if (range == null || !range.startsWith(BYTES_PREFIX)) {
			if (etag.equals(headers.get("if-none-match")))
				res = new Response(Response.Status.NOT_MODIFIED, mime, "");
			else {
				res = new Response(Response.Status.OK, mime, baseStream);
				res.addHeader("ETag", etag);
			}
		} else {
			int start = 0;
			int end = -1;
			final String bytes = range.substring(BYTES_PREFIX.length());
			final int minus = bytes.indexOf('-');
			if (minus > 0) {
				try {
					start = Integer.parseInt(bytes.substring(0, minus));
					final String endString = bytes.substring(minus + 1).trim();
					if (!"".equals(endString)) {
						end = Integer.parseInt(endString);
					}
				} catch (NumberFormatException e) {
				}
			}
			if (start >= fileLength) {
				res = new Response(
					Response.Status.RANGE_NOT_SATISFIABLE,
					MimeType.TEXT_PLAIN.toString(),
					""
				);
				res.addHeader("ETag", etag);
				res.addHeader("Content-Range", "bytes 0-0/" + fileLength);
			} else {
				if (end == -1 || end >= fileLength) {
					end = fileLength - 1;
				}
				res = new Response(
					Response.Status.PARTIAL_CONTENT,
					mime,
					new SliceInputStream(baseStream, start, end - start + 1)
				);
				res.addHeader("ETag", etag);
				res.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
			}
		}

		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	private Response notFound(String uri) {
		return new Response(
			Response.Status.NOT_FOUND,
			MimeType.TEXT_HTML.toString(),
			"<html><body><h1>Not found: " + uri + "</h1></body></html>"
		);
	}

	private Response forbidden(String uri, Throwable t) {
		t.printStackTrace();
		return new Response(
			Response.Status.FORBIDDEN,
			MimeType.TEXT_HTML.toString(),
			"<html><body><h1>" + t.getMessage() + "</h1>\n(" + uri + ")</body></html>"
		);
	}
}

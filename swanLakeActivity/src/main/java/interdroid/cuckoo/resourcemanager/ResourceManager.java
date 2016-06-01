/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package interdroid.cuckoo.resourcemanager;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for ResourceManagerProvider
 */
public final class ResourceManager {
	public static final String AUTHORITY = "interdroid.cuckoo.ResourceManager";

	// This class cannot be instantiated
	private ResourceManager() {
	}

	/**
	 * Resources table
	 */
	public static final class Resources implements BaseColumns {
		// This class cannot be instantiated
		private Resources() {
		}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/resources");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * resources.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.interdroid.cuckoo.resource";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * resource.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.interdroid.cuckoo.resource";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

		public static final String IDENTIFIER = "identifier";

		public static final String HUB_ADDRESS = "hubaddress";

		public static final String HOSTNAME = "hostname";

		public static final String BSSIDS = "bssids";

		public static final String UPLOAD = "upload";

		public static final String UPLOAD_VARIANCE = "upload_variance";

		public static final String DOWNLOAD = "download";

		public static final String DOWNLOAD_VARIANCE = "download_variance";

		public static final String LOCATION_LATITUDE = "location_latitude";

		public static final String LOCATION_LONGITUDE = "location_longitude";

	}

	/**
	 * Resource Bindings table
	 */
	public static final class ResourceBindings implements BaseColumns {
		// This class cannot be instantiated
		private ResourceBindings() {
		}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/resources/bindings");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * resources.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.interdroid.cuckoo.resource";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * resource.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.interdroid.cuckoo.resource";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

		/**
		 * The resource's identifier
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String IDENTIFIER = "identifier";

		/**
		 * The id bound to the identifier
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String BIND_ID = "bind_id";
	}
}

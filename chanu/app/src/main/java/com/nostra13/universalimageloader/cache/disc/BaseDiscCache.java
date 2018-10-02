/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.cache.disc;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;

/**
 * Base disc cache. Implements common functionality for disc cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 * @see DiscCacheAware
 * @see FileNameGenerator
 */
public abstract class BaseDiscCache implements DiscCacheAware {

	private static final String ERROR_ARG_NULL = "\"%s\" argument must be not null";

	protected File cacheDir;

	private FileNameGenerator fileNameGenerator;

	public BaseDiscCache(File cacheDir) {
		this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator());
	}

	public BaseDiscCache(File cacheDir, FileNameGenerator fileNameGenerator) {
		if (cacheDir == null) {
			throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
		}
		if (fileNameGenerator == null) {
			throw new IllegalArgumentException("fileNameGenerator" + ERROR_ARG_NULL);
		}

		this.cacheDir = cacheDir;
		this.fileNameGenerator = fileNameGenerator;
	}

	@Override
	public File get(String key) {
		com.mijack.Xlog.logMethodEnter("java.io.File com.nostra13.universalimageloader.cache.disc.BaseDiscCache.get(java.lang.String)",this,key);try{String fileName = fileNameGenerator.generate(key);
		{com.mijack.Xlog.logMethodExit("java.io.File com.nostra13.universalimageloader.cache.disc.BaseDiscCache.get(java.lang.String)",this);return new File(cacheDir, fileName);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.io.File com.nostra13.universalimageloader.cache.disc.BaseDiscCache.get(java.lang.String)",this,throwable);throw throwable;}
	}

	@Override
	public void clear() {
		com.mijack.Xlog.logMethodEnter("void com.nostra13.universalimageloader.cache.disc.BaseDiscCache.clear()",this);try{File[] files = cacheDir.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}com.mijack.Xlog.logMethodExit("void com.nostra13.universalimageloader.cache.disc.BaseDiscCache.clear()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.nostra13.universalimageloader.cache.disc.BaseDiscCache.clear()",this,throwable);throw throwable;}
	}
}
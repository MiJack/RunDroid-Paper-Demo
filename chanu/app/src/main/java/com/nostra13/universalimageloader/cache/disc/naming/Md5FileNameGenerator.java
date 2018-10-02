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
package com.nostra13.universalimageloader.cache.disc.naming;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.nostra13.universalimageloader.utils.L;

/**
 * Names image file as MD5 hash of image URI
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.4.0
 */
public class Md5FileNameGenerator implements FileNameGenerator {

	private static final String HASH_ALGORITHM = "MD5";
	private static final int RADIX = 10 + 26; /*// 10 digits + 26 letters*/

	@Override
	public String generate(String imageUri) {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator.generate(java.lang.String)",this,imageUri);try{byte[] md5 = getMD5(imageUri.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		{com.mijack.Xlog.logMethodExit("java.lang.String com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator.generate(java.lang.String)",this);return bi.toString(RADIX);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator.generate(java.lang.String)",this,throwable);throw throwable;}
	}

	private byte[] getMD5(byte[] data) {
		com.mijack.Xlog.logMethodEnter("[byte com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator.getMD5([byte)",this,data);try{byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			L.e(e);
		}
		{com.mijack.Xlog.logMethodExit("[byte com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator.getMD5([byte)",this);return hash;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator.getMD5([byte)",this,throwable);throw throwable;}
	}
}

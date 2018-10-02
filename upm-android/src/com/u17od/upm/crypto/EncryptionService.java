/*
 * Universal Password Manager
 * Copyright (c) 2010-2011 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.u17od.upm.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


public class EncryptionService {

    private static final String PBEWithSHA256And256BitAES = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final String randomAlgorithm = "SHA1PRNG";
    public static final int SALT_LENGTH = 8;
    public static final int SALT_GEN_ITER_COUNT = 20;

    private Cipher encryptionCipher; 
    private Cipher decryptionCipher;
    private SecretKey secretKey;
    private byte salt[];


    public EncryptionService(SecretKey secretKey, byte salt[]) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        this.secretKey = secretKey;
        this.salt = salt;
        initCiphers();
    }


    public EncryptionService(char[] password, byte[] salt) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBEWithSHA256And256BitAES);
        secretKey = keyFac.generateSecret(pbeKeySpec);

        this.salt = salt;

        initCiphers();
    }

    
    public EncryptionService(char[] password) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBEWithSHA256And256BitAES);
        secretKey = keyFac.generateSecret(pbeKeySpec);

        SecureRandom saltGen = SecureRandom.getInstance(randomAlgorithm);
        this.salt = new byte[SALT_LENGTH];
        saltGen.nextBytes(this.salt);

        initCiphers();
    }


    private void initCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.crypto.EncryptionService.initCiphers()",this);try{PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, SALT_GEN_ITER_COUNT);

        encryptionCipher = Cipher.getInstance(PBEWithSHA256And256BitAES);
        decryptionCipher = Cipher.getInstance(PBEWithSHA256And256BitAES);

        encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);com.mijack.Xlog.logMethodExit("void com.u17od.upm.crypto.EncryptionService.initCiphers()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.crypto.EncryptionService.initCiphers()",this,throwable);throw throwable;}
    }


    public byte[] encrypt(byte[] cleartext) throws IllegalBlockSizeException, BadPaddingException {
        com.mijack.Xlog.logMethodEnter("[byte com.u17od.upm.crypto.EncryptionService.encrypt([byte)",this,cleartext);try{com.mijack.Xlog.logMethodExit("[byte com.u17od.upm.crypto.EncryptionService.encrypt([byte)",this);return encryptionCipher.doFinal(cleartext);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.u17od.upm.crypto.EncryptionService.encrypt([byte)",this,throwable);throw throwable;}
    }


    public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, InvalidPasswordException {
        com.mijack.Xlog.logMethodEnter("[byte com.u17od.upm.crypto.EncryptionService.decrypt([byte)",this,ciphertext);try{byte[] retVal;
        try {
            retVal = decryptionCipher.doFinal(ciphertext);
        } catch (BadPaddingException e) {
            throw new InvalidPasswordException(); 
        }
        {com.mijack.Xlog.logMethodExit("[byte com.u17od.upm.crypto.EncryptionService.decrypt([byte)",this);return retVal;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.u17od.upm.crypto.EncryptionService.decrypt([byte)",this,throwable);throw throwable;}
    }


    public byte[] getSalt() {
        com.mijack.Xlog.logMethodEnter("[byte com.u17od.upm.crypto.EncryptionService.getSalt()",this);try{com.mijack.Xlog.logMethodExit("[byte com.u17od.upm.crypto.EncryptionService.getSalt()",this);return salt;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.u17od.upm.crypto.EncryptionService.getSalt()",this,throwable);throw throwable;}
    }


    public SecretKey getSecretKey() {
        com.mijack.Xlog.logMethodEnter("javax.crypto.SecretKey com.u17od.upm.crypto.EncryptionService.getSecretKey()",this);try{com.mijack.Xlog.logMethodExit("javax.crypto.SecretKey com.u17od.upm.crypto.EncryptionService.getSecretKey()",this);return secretKey;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("javax.crypto.SecretKey com.u17od.upm.crypto.EncryptionService.getSecretKey()",this,throwable);throw throwable;}
    }

    
    public static SecretKey createSecretKey(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        com.mijack.Xlog.logStaticMethodEnter("javax.crypto.SecretKey com.u17od.upm.crypto.EncryptionService.createSecretKey([char)",password);try{PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBEWithSHA256And256BitAES);
        {com.mijack.Xlog.logStaticMethodExit("javax.crypto.SecretKey com.u17od.upm.crypto.EncryptionService.createSecretKey([char)");return keyFac.generateSecret(pbeKeySpec);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("javax.crypto.SecretKey com.u17od.upm.crypto.EncryptionService.createSecretKey([char)",throwable);throw throwable;}
    }

}

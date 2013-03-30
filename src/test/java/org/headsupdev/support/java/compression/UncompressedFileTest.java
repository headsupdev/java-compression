/*
 * Copyright 2012 Heads Up Development Ltd.
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

package org.headsupdev.support.java.compression;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

/**
 * UncompressedFile Tester.
 */
public class UncompressedFileTest
        extends TestCase
{

    private UncompressedFile testFile;

    public UncompressedFileTest( String name )
    {
        super( name );
    }

    public void setUp()
            throws Exception
    {
        testFile = new UncompressedFile( "target/test.txt" );
        Writer out = new FileWriter( testFile );
        out.write( "This is some text - a test\n\nthanks\n" );
        out.close();
    }

    public void tearDown()
            throws Exception
    {
        testFile.delete();
    }

    public void testCopy()
            throws IOException
    {
        UncompressedFile copy = testFile.copyTo( "target/testcopy.txt" );
        assertTrue( copy.exists() );
        assertEquals( copy.length(), testFile.length() );

        copy.delete();
    }

    public void testZip()
            throws Exception
    {
        ZipFile zip = testFile.zipCompress( false );
        assertTrue( zip.exists() );
        assertTrue( zip.length() > 0 );

        zip.delete();
    }

    public void testZipFiles()
            throws Exception
    {
        ZipFile zip = UncompressedFile.zipCompress( new UncompressedFile[]{ testFile }, false );
        assertTrue( zip.exists() );
        assertTrue( zip.length() > 0 );

        zip.delete();
    }

    public void testTar()
            throws Exception
    {
        TarFile tar = testFile.tarCompress( false );
        assertTrue( tar.exists() );
        assertTrue( tar.length() > 0 );

        tar.delete();
    }

    public void testTarFiles()
            throws Exception
    {
        TarFile tar = UncompressedFile.tarCompress( new UncompressedFile[]{ testFile }, false );
        assertTrue( tar.exists() );
        assertTrue( tar.length() > 0 );

        tar.delete();
    }

    public void testGZip()
            throws Exception
    {
        GZipFile gzip = testFile.gzipCompress( false );
        assertTrue( gzip.exists() );
        assertTrue( gzip.length() > 0 );

        gzip.delete();
    }

    public void testRecursiveSimlink()
            throws Exception
    {
        UncompressedFile recursiveLink = new UncompressedFile( "src/test/resources/recursive" );
        assertTrue( recursiveLink.exists() );
        ZipFile zippedFile = recursiveLink.zipCompress();
        assertTrue( zippedFile.exists() );

        zippedFile.delete();
    }

    public static Test suite()
    {
        return new TestSuite( UncompressedFileTest.class );
    }
}

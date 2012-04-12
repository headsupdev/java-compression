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

import java.io.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class ZipFile
        extends UncompressedFile
{

    final int BUFFER = 2048;

    public ZipFile( String name )
    {
        super( name );
    }

    public ZipFile( String parent, String child )
    {
        super( parent, child );
    }

    public ZipFile( java.io.File file )
    {
        super( file.getPath() );
    }

    public ZipFile( java.io.File parent, String child )
    {
        super( parent, child );
    }

    public UncompressedFile expand() throws IOException
    {
        byte data[] = new byte[BUFFER];
        String destFile = getAbsolutePath();
        if ( destFile.endsWith( ".zip" ) )
        {
            destFile = destFile.substring( 0, destFile.length() - 4 );
        }
        else
        {
            destFile = destFile + "_contents";
        }

        UncompressedFile ret = new UncompressedFile( destFile );
        ZipInputStream zin = null;
        try
        {
            ret.mkdir();
            zin = new ZipInputStream( new FileInputStream( this ) );

            ZipEntry entry;
            while ( (entry = zin.getNextEntry()) != null )
            {
                UncompressedFile output = new UncompressedFile( destFile, entry.getName() );

                if ( entry.isDirectory() )
                {
                    output.mkdir();
                }
                else
                {
                    BufferedOutputStream out = null;
                    try
                    {
                        out = new BufferedOutputStream(
                                new FileOutputStream( output ), BUFFER );

                        int count;
                        while ( (count = zin.read( data, 0, BUFFER )) != -1 )
                        {
                            out.write( data, 0, count );
                        }
                    }
                    finally
                    {
                        if ( out != null )
                        {
                            out.close();
                        }
                    }
                }

                zin.closeEntry();
            }

        }
        catch ( IOException e )
        {
            throw new IOException( e.getMessage() );
        }
        finally
        {
            if ( zin != null )
            {
                zin.close();
            }
        }

        return ret;
    }

}

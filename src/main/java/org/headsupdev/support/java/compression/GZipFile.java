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
import java.util.zip.GZIPInputStream;

public class GZipFile
        extends UncompressedFile
{

    final int BUFFER = 2048;

    public GZipFile( String name )
    {
        super( name );
    }

    public GZipFile( String parent, String child )
    {
        super( parent, child );
    }

    public GZipFile( java.io.File file )
    {
        super( file.getPath() );
    }

    public GZipFile( java.io.File parent, String child )
    {
        super( parent, child );
    }

    public UncompressedFile expand() throws IOException
    {
        return expand( false );
    }

    public UncompressedFile expand( boolean delete ) throws IOException
    {
        String dest = this.getPath();
        dest = dest.substring( 0, dest.length() - 3 );

        return this.expandTo( dest, delete );
    }

    public UncompressedFile expandTo( String dest ) throws IOException
    {
        return expandTo( dest, false );
    }

    public UncompressedFile expandTo( String dest, boolean delete ) throws IOException
    {

        InputStream zis = null;
        try
        {
            FileInputStream fileIn = new FileInputStream( this );
            zis = new GZIPInputStream( new BufferedInputStream( fileIn ) );

            int count;
            byte data[] = new byte[BUFFER];

            BufferedOutputStream out = null;
            try
            {
                out = new BufferedOutputStream( new FileOutputStream( dest ), BUFFER );
                while ( (count = zis.read( data, 0, BUFFER )) != -1 )
                {
                    out.write( data, 0, count );
                }

                out.flush();
            }
            finally
            {
                if ( out != null )
                {
                    out.close();
                }
            }
        }
        catch ( Exception e )
        {
            throw new IOException( e.getMessage() );
        }
        finally
        {
            if ( zis != null )
            {
                zis.close();
            }
        }

        if ( delete )
        {
            this.delete();
        }

        return new UncompressedFile( dest );
    }

}

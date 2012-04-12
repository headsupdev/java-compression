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

import com.ice.tar.TarArchive;

import java.io.FileInputStream;
import java.io.IOException;

public class TarFile
        extends UncompressedFile
{

    public TarFile( String name )
    {
        super( name );
    }

    public TarFile( String parent, String child )
    {
        super( parent, child );
    }

    public TarFile( java.io.File file )
    {
        super( file.getPath() );
    }

    public TarFile( java.io.File parent, String child )
    {
        super( parent, child );
    }

    public UncompressedFile expand() throws IOException
    {
        String destFile = getAbsolutePath();
        if ( destFile.endsWith( ".tar" ) )
        {
            destFile = destFile.substring( 0, destFile.length() - 4 );
        }
        else
        {
            destFile = destFile + "_contents";
        }

        UncompressedFile ret = new UncompressedFile( destFile );
        TarArchive archive = null;
        try
        {
            ret.mkdir();
            archive = new TarArchive( new FileInputStream( this ) );

            archive.setDebug( false );
            archive.setVerbose( false );
            archive.setKeepOldFiles( false );
            archive.setAsciiTranslation( false );

            archive.extractContents( ret );
        }
        catch ( IOException e )
        {
            throw new IOException( e.getMessage() );
        }
        finally
        {
            if ( archive != null )
            {
                archive.closeArchive();
            }
        }

        return ret;
    }

}

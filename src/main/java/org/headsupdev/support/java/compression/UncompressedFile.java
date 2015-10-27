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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class UncompressedFile
        extends java.io.File
{

    private static final int BUFFER = 2048;

    public UncompressedFile( String name )
    {
        super( name );
    }

    public UncompressedFile( String parent, String child )
    {
        super( parent, child );
    }

    public UncompressedFile( java.io.File file )
    {
        super( file.getPath() );
    }

    public UncompressedFile( java.io.File parent, String child )
    {
        super( parent, child );
    }

    public ZipFile zipCompress() throws IOException
    {
        return this.zipCompress( false );
    }

    public ZipFile zipCompress( boolean delete ) throws IOException
    {

        String dest = this.getAbsolutePath() + ".zip";

        ZipArchiveOutputStream zout = null;
        try
        {
            zout = new ZipArchiveOutputStream( new File( dest ) );

            zipCompress( this, "", delete, zout );
        }
        finally
        {
            if ( zout != null )
            {
                zout.close();
            }
        }

        return new ZipFile( dest );
    }

    private static void zipCompress( java.io.File file, String prefix,
                                     boolean delete, ZipArchiveOutputStream zout ) throws IOException
    {
        byte data[] = new byte[BUFFER];

        if ( file.isDirectory() )
        {
            String thisDir = prefix + file.getName() + separatorChar;
            zout.putArchiveEntry( new ZipArchiveEntry( thisDir ) );
            zout.closeArchiveEntry();
            java.io.File[] files = file.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                /* FIXME add a recursive flag */
                zipCompress( files[i], thisDir, delete, zout );
            }
        }
        else
        {
            BufferedInputStream origin = null;
            try
            {
                origin = new BufferedInputStream(
                        new FileInputStream( file ), BUFFER );

                ZipArchiveEntry entry = new ZipArchiveEntry( prefix + file.getName() );
                zout.putArchiveEntry( entry );
                int count;
                while ( ( count = origin.read( data, 0, BUFFER ) ) != -1 )
                {
                    zout.write( data, 0, count );
                }
                zout.closeArchiveEntry();
            }
            catch ( FileNotFoundException e )
            {
                if ( !e.getMessage().contains( "(Too many levels of symbolic links)" ) )
                {
                    throw e;
                }
            }
            finally
            {
                if ( origin != null )
                {
                    origin.close();
                }
            }
        }

        if ( delete )
        {
            file.delete();
        }
    }

    /**
     * Create a zip archive of the listed files. It will be created in the same
     * directory as the first listed file. The file will be called "data.zip".
     *
     * @param files  The files to archive
     * @param delete True if the files should be deleted after they are added to the archive
     * @return A ZipFile representing the new archive, or null if there are no files to archive
     * @throws IOException If there is a problem reading the files or writing the archive
     */
    public static ZipFile zipCompress( UncompressedFile[] files, boolean delete ) throws IOException
    {
        if ( files == null || files.length == 0 )
        {
            return null;
        }
        UncompressedFile dest = new UncompressedFile( getParentPath( files[0] ), "data.zip" );

        ZipArchiveOutputStream zout = null;
        try
        {
            zout = new ZipArchiveOutputStream( dest );

            for ( int i = 0; i < files.length; i++ )
            {
                zipCompress( files[i], "", delete, zout );
            }
        }
        finally
        {
            if ( zout != null )
            {
                zout.close();
            }
        }

        return new ZipFile( dest );
    }

    public GZipFile gzipCompress() throws IOException
    {
        return this.gzipCompress( false );
    }

    public GZipFile gzipCompress( boolean delete ) throws IOException
    {
        String dest = this.toString() + ".gz";

        InputStream is = null;
        try
        {
            FileInputStream fileIn = new FileInputStream( this );

            is = new BufferedInputStream( fileIn );

            int count;
            byte data[] = new byte[BUFFER];

            GZIPOutputStream zout = null;
            try
            {
                zout = new GZIPOutputStream( new BufferedOutputStream( new FileOutputStream( dest ),
                        BUFFER ) );

                while ( (count = is.read( data, 0, BUFFER )) != -1 )
                {
                    zout.write( data, 0, count );
                }
                zout.flush();
            }
            finally
            {
                if ( zout != null )
                {
                    zout.close();
                }
            }

            if ( delete )
            {
                this.delete();
            }
        }
        catch ( Exception e )
        {
            throw new IOException( e.getMessage() );
        }
        finally
        {
            if ( is != null )
            {
                is.close();
            }
        }

        return new GZipFile( dest );
    }

    public TarFile tarCompress() throws IOException
    {
        return this.tarCompress( false );
    }

    public TarFile tarCompress( boolean delete ) throws IOException
    {
        String dest = this.toString() + ".tar";

        TarArchive archive = null;
        try
        {
            archive = new TarArchive( new BufferedOutputStream(
                    new FileOutputStream( dest ) ), BUFFER );

            archive.setDebug( false );
            archive.setVerbose( false );
            archive.setKeepOldFiles( false );
            archive.setAsciiTranslation( false );

            archive.setUserInfo( 0, "", 0, "" );

            tarCompress( this, "", delete, archive );
        }
        finally
        {
            if ( archive != null )
            {
                archive.closeArchive();
            }
        }

        return new TarFile( dest );
    }

    private static void tarCompress( java.io.File file, String prefix, boolean delete,
                                     TarArchive archive ) throws IOException
    {
        String entryName = prefix + file.getName();
        if ( file.isDirectory() )
        {
            entryName += UncompressedFile.separatorChar;
            java.io.File[] files = file.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                tarCompress( files[i], entryName, delete, archive );
            }
        }

        com.ice.tar.TarEntry entry = new com.ice.tar.TarEntry( file );
        entry.setName( entryName );
        archive.writeEntry( entry, false );

        if ( delete )
        {
            file.delete();
        }
    }

    /**
     * Create a tar archive of the listed files. It will be created in the same
     * directory as the first listed file. The file will be called "data.tar".
     *
     * @param files  The files to archive
     * @param delete True if the files should be deleted after they are added to the archive
     * @return A TarFile representing the new archive, or null if there are no files to archive
     * @throws IOException If there is a problem reading the files or writing the archive
     */
    public static TarFile tarCompress( UncompressedFile[] files, boolean delete ) throws IOException
    {
        if ( files == null || files.length == 0 )
        {
            return null;
        }
        UncompressedFile dest = new UncompressedFile( getParentPath( files[0] ), "data.tar" );

        TarArchive archive = null;
        try
        {
            archive = new TarArchive( new BufferedOutputStream(
                    new FileOutputStream( dest ) ), BUFFER );

            archive.setDebug( false );
            archive.setVerbose( false );
            archive.setKeepOldFiles( false );
            archive.setAsciiTranslation( false );

            archive.setUserInfo( 0, "", 0, "" );

            for ( int i = 0; i < files.length; i++ )
            {
                tarCompress( files[i], "", delete, archive );
            }
        }
        finally
        {
            if ( archive != null )
            {
                archive.closeArchive();
            }
        }

        return new TarFile( dest );
    }

    public UncompressedFile moveTo( String dest ) throws IOException
    {
        return moveTo( new UncompressedFile( dest ) );
    }

    public UncompressedFile moveTo( UncompressedFile dest ) throws IOException
    {
        UncompressedFile ret = copyTo( dest );
        this.delete();
        return ret;
    }

    public UncompressedFile copyTo( String dest ) throws IOException
    {
        return copyTo( new UncompressedFile( dest ) );
    }

    public UncompressedFile copyTo( UncompressedFile dest ) throws IOException
    {

        if ( isDirectory() )
        {
            return copyDirTo( dest );
        }

        BufferedInputStream is = null;
        try
        {
            is = new BufferedInputStream( new FileInputStream( this ) );

            int count;
            byte data[] = new byte[BUFFER];

            BufferedOutputStream out = null;
            try
            {
                out = new BufferedOutputStream( new FileOutputStream( dest ), BUFFER );
                while ( (count = is.read( data, 0, BUFFER )) != -1 )
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
            if ( is != null )
            {
                is.close();
            }
        }

        return new UncompressedFile( dest );
    }

    public UncompressedFile copyDirTo( UncompressedFile dest ) throws IOException
    {
        dest.mkdir();

        java.io.File[] list = listFiles();
        for ( int i = 0; i < list.length; i++ )
        {
            UncompressedFile next = new UncompressedFile( list[i] );
            if ( next.getName().equals( "." ) || next.getName().equals( ".." ) )
            {
                continue;
            }

            UncompressedFile subDest = new UncompressedFile( dest, next.getName() );
            if ( next.isDirectory() )
            {
                next.copyDirTo( subDest );
            }
            else
            {
                next.copyTo( subDest );
            }
        }
        return new UncompressedFile( dest );
    }

    private static String getParentPath( UncompressedFile file )
    {
        if ( file.getParentFile() != null )
        {
            return file.getParentFile().getAbsolutePath();
        }

        return (new UncompressedFile( "" )).getAbsolutePath();
    }
}

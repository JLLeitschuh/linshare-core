/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.objects;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SignatureVo;
import org.linagora.linShare.core.utils.ArchiveZipStream;

public class FileStreamResponse implements StreamResponse{

	private InputStream inputStream;
	private int size;
	private String contentType;
	private String fileName;
	
	private static final String BINARY_MIME_TYPE  = "application/octet-stream";
	
	
	public FileStreamResponse(DocumentVo documentVo,InputStream stream){
		this.inputStream=stream;
		this.size = documentVo.getSize().intValue();
		this.fileName=documentVo.getFileName();
		
		this.contentType = BINARY_MIME_TYPE; //always save the doc before reading it ....
		//this.contentType=documentVo.getType();
	}
	
	public FileStreamResponse(SignatureVo signatureVo,InputStream stream){
		this.inputStream=stream;
		this.size = signatureVo.getSize().intValue();
		
		//we want unicity of the name of file
		//so we put this name signed_fileName.ext_idDatabase.xml
		this.fileName=signatureVo.getName()+"_"+signatureVo.getPersistenceId()+".xml"; 
		this.contentType=Signature.MIMETYPE;
	}
	
	public FileStreamResponse(ArchiveZipStream stream,String filenameResponse){
		
		this.inputStream=stream;
		this.size=(int) stream.getTempFile().length();
		
		if(filenameResponse==null)
		this.fileName=ArchiveZipStream.ARCHIVE_ZIP_DOWNLOAD_NAME;
		else 
		this.fileName=filenameResponse;
		
		this.contentType=BINARY_MIME_TYPE;
	}
	
	
	
	// *** getters/setters

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// *** methods for the interface StreamResponse implementation !!!
	
	public String getContentType() {
		return contentType;
	}
	
	public InputStream getStream() throws IOException {
		return inputStream;
	}

	public void prepareResponse(Response response) {
        
		//BUG WITH IE WHEN PRAGMA IS NO-CACHE solution is:
        //The proper solution to IE cache issues is to declare the attachment as "Pragma: private"
        //and "Cache-Control: private, must-revalidate" in the HTTP Response.
        //This allows MS-IE to save the content as a temporary file in its local cache,
        //but in not general public cache servers, before handing it off the plugin, e.g. Adobe Acrobat, to handle it.
		
		response.setContentLength(this.size);
        response.setHeader("Content-disposition", "attachment; filename=\""+this.fileName+"\"");
        response.setHeader("Content-Transfer-Encoding","none");
        
        //Pragma is a HTTP 1.0 directive that was retained in HTTP 1.1 for backward compatibility.
        //no-cache prevent caching in proxy
        response.setHeader("Pragma","private"); 
        
        //�cache-control: private�. It instructs proxies in the path not to cache the page. But it permits browsers to cache the page.
        //must-revalidate means the browser must revalidate the page against the server before serving it from cache
        
        
        //post-check Defines an interval in seconds after which an entity must be checked for freshness.
        //The check may happen after the user is shown the resource but ensures that on the next roundtrip
        //the cached copy will be up-to-date
        //pre-check Defines an interval in seconds after
        //which an entity must be checked for freshness prior to showing the user the resource.
        
        response.setHeader("Cache-Control","private,must-revalidate, post-check=0, pre-check=0");
        
        //Cache Control: max-age is the same as Expires header
        //Setting max-age to zero ensures that a page is never served from cache, but is always re-validated against the server
        
        //response.setHeader("Cache-Control","private,must-revalidate,max-age=0,post-check=0, pre-check=0");
        //response.setIntHeader("Expires", 0); //HTTP 1.0 directive that was retained for backward compatibility
	}
}

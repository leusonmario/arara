/**
 * Arara, the cool TeX automation tool
 * Copyright (c) 2012 -- 2017, Paulo Roberto Massa Cereda 
 * All rights reserved.
 *
 * Redistribution and  use in source  and binary forms, with  or without
 * modification, are  permitted provided  that the  following conditions
 * are met:
 *
 * 1. Redistributions  of source  code must  retain the  above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form  must reproduce the above copyright
 * notice, this list  of conditions and the following  disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither  the name  of the  project's author nor  the names  of its
 * contributors may be used to  endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS  PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS
 * "AS IS"  AND ANY  EXPRESS OR IMPLIED  WARRANTIES, INCLUDING,  BUT NOT
 * LIMITED  TO, THE  IMPLIED WARRANTIES  OF MERCHANTABILITY  AND FITNESS
 * FOR  A PARTICULAR  PURPOSE  ARE  DISCLAIMED. IN  NO  EVENT SHALL  THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE  LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY,  OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT  NOT LIMITED  TO, PROCUREMENT  OF SUBSTITUTE  GOODS OR  SERVICES;
 * LOSS  OF USE,  DATA, OR  PROFITS; OR  BUSINESS INTERRUPTION)  HOWEVER
 * CAUSED AND  ON ANY THEORY  OF LIABILITY, WHETHER IN  CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY  OUT  OF  THE USE  OF  THIS  SOFTWARE,  EVEN  IF ADVISED  OF  THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.cereda.arara.utils;

import com.github.cereda.arara.controller.LanguageController;
import com.github.cereda.arara.model.AraraException;
import com.github.cereda.arara.model.Messages;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * Implements the template merging from Apache Velocity.
 * @author Paulo Roberto Massa Cereda
 * @version 4.0
 * @since 4.0
 */
public class VelocityUtils {
    
    // the language controller
    private static final LanguageController messages =
            LanguageController.getInstance();
    
    /**
     * Merges the provided template with the context map and writes the result
     * in an output file. The operation relies on the Apache Velocity project.
     * @param input The input file.
     * @param output The output file.
     * @param map The context map.
     * @throws AraraException Something terribly wrong happened, to be caught
     * in the higher levels.
     */
    public static void mergeVelocityTemplate(File input, File output,
            Map<String, Object> map) throws AraraException {
        
        // let us try
        try {
            
            // create the template engine instance
            VelocityEngine engine = new VelocityEngine();
            
            // use the resource path trick: set the default
            // location to the input file's parent directory,
            // so our file is easily located
            engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                    input.getCanonicalFile().getParent());
            
            // set the logging feature of Apache Velocity to
            // register the occurrences in a null provider
            // (we do not want unnecessary verbose output)
            engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                    "org.apache.velocity.runtime.log.NullLogSystem");
            
            // init the engine with the
            // provided settings
            engine.init();
            
            // create a context for Apache Velocity,
            // based on the provided map
            VelocityContext context = new VelocityContext(map);
            
            // get the template from the engine and
            // read it as an UTF-8 file
            Template template = engine.getTemplate(input.getName(), "UTF-8");
            
            // create an output stream from
            // the file output reference
            FileOutputStream stream = new FileOutputStream(output);
            
            // create a writer based on the
            // previously created stream
            Writer writer = new OutputStreamWriter(stream, "UTF-8");
            
            // merge the context map with the
            // template file and write the result
            // to the output stream writer
            template.merge(context, writer);
            
            // close both writer
            // and output stream
            writer.close();
            stream.close();
            
        } catch(ResourceNotFoundException rnfexception) {
            throw new AraraException(
                    messages.getMessage(
                            Messages.ERROR_VELOCITY_FILE_NOT_FOUND
                    ),
                    rnfexception
            );
        } catch(ParseErrorException peexception) {
            throw new AraraException(
                    messages.getMessage(
                            Messages.ERROR_VELOCITY_PARSE_EXCEPTION
                    ),
                    peexception
            );
        } catch(MethodInvocationException miexception) {
            throw new AraraException(
                    messages.getMessage(
                            Messages.ERROR_VELOCITY_METHOD_INVOCATION_EXCEPTION
                    ),
                    miexception
            );
        } catch (IOException ioexception) {
            throw new AraraException(
                    messages.getMessage(
                            Messages.ERROR_VELOCITY_FILE_NOT_FOUND
                    ),
                    ioexception
            );
        }
        
    }
    
}

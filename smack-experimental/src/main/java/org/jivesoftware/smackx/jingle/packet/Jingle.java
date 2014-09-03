/**
 *
 * Copyright 2003-2007 Jive Software, 2014 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.jingle.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.XmlStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An Jingle sub-packet, which is used by XMPP clients to exchange info like
 * descriptions and transports.
 * <p>
 * The following link summarizes the requirements of Jingle IM: <a
 * href="http://www.xmpp.org/extensions/jep-0166.html">Valid tags</a>.
 * </p>
 *
 * @author Alvaro Saurin
 * @author Florian Schmaus
 */
public class Jingle extends IQ {

    public static final String NAMESPACE = "urn:xmpp:jingle:1";

    public static final String ELEMENT = "jingle";

    /**
     * The session ID related to this session. The session ID is a unique
     * identifier generated by the initiator. This should match the XML Nmtoken
     * production so that XML character escaping is not needed for characters
     * such as &.
     */
    private final String sid;

    /**
     * The jingle action. This attribute is required.
     */
    private final JingleAction action;

    private String initiator; // The initiator as a "user@host/resource"

    private String responder; // The responder

    private final List<JingleContent> contents = new ArrayList<JingleContent>();

    public Jingle(String sid, JingleAction action) {
        this.sid = sid;
        this.action = action;
    }
    
    public Jingle(String sid, JingleAction action, List<JingleContent> contents) {
        this(sid, action);
        this.contents.addAll(contents);
    }


    /**
     * Returns the session ID related to the session. The session ID is a unique
     * identifier generated by the initiator. This should match the XML Nmtoken
     * production so that XML character escaping is not needed for characters
     * such as &.
     *
     * @return Returns the session ID related to the session.
     */
    public String getSid() {
        return sid;
    }

    /**
     * Get an iterator for the contents
     *
     * @return the contents
     */
    public List<JingleContent> getContents() {
        synchronized (contents) {
            return Collections.unmodifiableList(new ArrayList<JingleContent>(contents));
        }
    }

    /**
     * Add a new content.
     *
     * @param content the content to add
     */
    public void addContent(final JingleContent content) {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        synchronized (contents) {
            contents.add(content);
        }
    }

    /**
     * Add a list of JingleContent elements
     *
     * @param contentList the list of contents to add
     */
    public void addContents(final List<JingleContent> contentList) {
        synchronized (contents) {
            contents.addAll(contentList);
        }
    }

     /**
     * Get the action specified in the packet
     *
     * @return the action
     */
    public JingleAction getAction() {
        return action;
    }

    /**
     * Get the initiator. The initiator will be the full JID of the entity that
     * has initiated the flow (which may be different to the "from" address in
     * the IQ)
     *
     * @return the initiator
     */
    public String getInitiator() {
        return initiator;
    }

    /**
     * Set the initiator. The initiator must be the full JID of the entity that
     * has initiated the flow (which may be different to the "from" address in
     * the IQ)
     *
     * @param initiator the initiator to set
     */
    public void setInitiator(final String initiator) {
        this.initiator = initiator;
    }

    /**
     * Get the responder. The responder is the full JID of the entity that has
     * replied to the initiation (which may be different to the "to" addresss in
     * the IQ).
     *
     * @return the responder
     */
    public String getResponder() {
        return responder;
    }

    /**
     * Set the responder. The responder must be the full JID of the entity that
     * has replied to the initiation (which may be different to the "to"
     * addresss in the IQ).
     *
     * @param resp the responder to set
     */
    public void setResponder(final String resp) {
        responder = resp;
    }

    /**
     * Get a hash key for the session this packet belongs to.
     *
     * @param sid       The session id
     * @param initiator The initiator
     * @return A hash key
     */
    public static int getSessionHash(final String sid, final String initiator) {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (initiator == null ? 0 : initiator.hashCode());
        result = PRIME * result + (sid == null ? 0 : sid.hashCode());
        return result;
    }

    /**
     * Return the XML representation of the packet.
     *
     * @return the XML string
     */
    @Override
    public XmlStringBuilder getChildElementXML() {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.prelude(ELEMENT, NAMESPACE);
        xml.optAttribute("initiator", getInitiator());
        xml.optAttribute("responder", getResponder());
        xml.optAttribute("action", getAction());
        xml.optAttribute("sid", getSid());
        xml.rightAngleBracket();
 
        synchronized (contents) {
            for (JingleContent content : contents) {
                xml.append(content.toXML());
            }
         }

        xml.closeElement(ELEMENT);
        return xml;
    }
}

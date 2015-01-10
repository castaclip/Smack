/**
 *
 * Copyright © 2015 Florian Schmaus
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
package org.jivesoftware.smackx.mam.packet;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.rsm.packet.RSMSet;


/**
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message Archive Management</a>
 *
 */
public class MamPacket {

    public static final String NAMESPACE = "urn:xmpp:mam:0";

    public static abstract class AbstractMamExtension implements PacketExtension {
        public final String queryId;

        protected AbstractMamExtension(String queryId) {
            this.queryId = queryId;
        }

        public final String getQueryId() {
            return queryId;
        }
        

        @Override
        public final String getNamespace() {
            return NAMESPACE;
        }

    }

    public static class MamFinExtension extends AbstractMamExtension {

        public static final String ELEMENT = "fin";

        public final RSMSet rsmSet;

        public MamFinExtension(String queryId, RSMSet rsmSet) {
            super(queryId);
            this.rsmSet = rsmSet;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(this);
            xml.optAttribute("queryid", queryId);
            if (rsmSet == null) {
                xml.closeEmptyElement();
            } else {
                xml.rightAngleBracket();
                xml.element(rsmSet);
                xml.closeElement(this);
            }
            return xml;
        }

        public static MamFinExtension from(Message message) {
            return message.getExtension(ELEMENT, NAMESPACE);
        }
    }

    public static class MamResultExtension extends AbstractMamExtension {

        public static final String ELEMENT = "result";

        private final String id;
        private final Forwarded forwarded;

        public MamResultExtension(String queryId, String id, Forwarded forwarded) {
            super(queryId);
            if (StringUtils.isNotEmpty(id)) {
                throw new IllegalArgumentException("id must not be null or empty");
            }
            if (forwarded == null) {
                throw new IllegalArgumentException("forwarded must no be null");
            }
            this.id = id;
            this.forwarded = forwarded;
        }

        public String getId() {
            return id;
        }

        public Forwarded getForwarded() {
            return forwarded;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public CharSequence toXML() {
            // TODO Auto-generated method stub
            return null;
        }

        public static MamResultExtension from(Message message) {
            return message.getExtension(ELEMENT, NAMESPACE);
        }

    }
}

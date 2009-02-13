/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.DDSimpl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.omg.dds.ContentFilteredTopic;
import org.omg.dds.DomainParticipant;
import org.omg.dds.Topic;

/**
 *
 * @author matt
 */
public class ContentFilteredTopicImpl extends TopicDescriptionImpl implements ContentFilteredTopic {
    
    protected String filter_expression;
    protected String[] expression_parameters;
    protected DomainParticipant dp;
    protected Topic related_topic;
    
    public ContentFilteredTopicImpl(DomainParticipant dp, Topic related_topic, String name, String filter_expression, String[] expression_parameters) {
        this(name, related_topic, filter_expression, expression_parameters);     
        
        this.dp = dp;
    }
    
    public ContentFilteredTopicImpl(String name, Topic related_topic, String filterExpression, String[] expression_parameters){
        super(name, related_topic.get_type_name());
        
        this.related_topic = related_topic;
        this.filter_expression = filterExpression;
        this.expression_parameters = expression_parameters;
    }

    public ContentFilteredTopicImpl() {
    }
    
    public Topic get_related_topic(){
        return null;
    }

    public String get_filter_expression(){
        return filter_expression;
    }
    
    public String[] get_expression_parameters(){
        return expression_parameters;
    }
    
    public void set_expression_parameters(String[] expression_parameters){
        this.expression_parameters = expression_parameters;
    }

    public DomainParticipant get_participant() {
        return dp;
    }

    public void set_participant(DomainParticipant dp) {
        this.dp = dp;
    }

    public void read(DataInputStream is) throws IOException {
        this.name = is.readUTF();
        this.type_name = is.readUTF();
        this.filter_expression = is.readUTF();
        
        int numParams = is.readInt();
        
        String[] expression_parameters = new String[numParams];

        for(int i=0;i<numParams;i++){
            expression_parameters[i] = is.readUTF();
        }
 
        set_expression_parameters(expression_parameters);
    }

    public void write(DataOutputStream os) throws IOException {
        os.writeUTF(name);
        os.writeUTF(type_name);
        os.writeUTF(filter_expression);
        
        int numParams = expression_parameters.length;
        os.writeInt(numParams);
        
        for(int i=0; i<numParams; i++){
            String expressionParam = expression_parameters[i];
            os.writeUTF(expressionParam);
        }
    }
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.DDSimpl;

import edu.umb.cs.tinydds.cl.DDSParser;
import edu.umb.cs.tinydds.cl.ParseException;
import java.io.DataInputStream;
import java.io.IOException;
import edu.umb.cs.tinydds.cl.DDSParser.Expression;
import edu.umb.cs.tinydds.utils.StringUtils;
import java.io.DataOutputStream;
import java.util.Hashtable;
import org.omg.dds.ContentFilteredTopic;
import org.omg.dds.DomainParticipant;
import org.omg.dds.Topic;

/**
 *  
 * 
 * @author matt
 */
public class SensorContentFilteredTopic extends TopicDescriptionImpl implements ContentFilteredTopic {

    protected Expression exp;
    protected ContentFilteredTopicImpl contentFilteredTopic;
    protected Hashtable selectors;
    
    /**
     * 
     * @param name
     * @param related_topic
     * @param filterExpression
     * @param expression_parameters
     */
    public SensorContentFilteredTopic(ContentFilteredTopic contentFilteredTopic) {
        super(contentFilteredTopic.get_name(), contentFilteredTopic.get_type_name());
        this.contentFilteredTopic = (ContentFilteredTopicImpl)contentFilteredTopic;
        parseFilterExpression();
    }

    public SensorContentFilteredTopic() {
        contentFilteredTopic = new ContentFilteredTopicImpl();
    }
    
    /**
     * 
     * @param filterExpression
     * @param expressionParams
     * @return
     */
    protected String buildFullExpressionString(String filterExpression, String[] expressionParams){
        
        for(int i=0; i<expressionParams.length;i++){
            filterExpression = StringUtils.replaceFirst(filterExpression, "%n", expressionParams[i]);
        }
        
        return filterExpression;
    }
    
    /**
     * 
     * @return
     */
    protected void parseFilterExpression(){
        
        String filterExpression = get_filter_expression();
        String[] expressionParams = get_expression_parameters();
        
        String fullExpressionString = buildFullExpressionString(filterExpression, expressionParams);
        
        DDSParser parser = new DDSParser(fullExpressionString);
        
        try {
            exp = parser.parse();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        selectors = parser.getSelectors();
    }

    public Hashtable getSelectors(){
        return selectors;
    }
    
    public String[] get_expression_parameters() {
        return contentFilteredTopic.get_expression_parameters();
    }

    public String get_filter_expression() {
        return contentFilteredTopic.get_filter_expression();
    }

    public Topic get_related_topic() {
        return contentFilteredTopic.get_related_topic();
    }

    public void set_expression_parameters(String[] expression_parameters) {
        contentFilteredTopic.set_expression_parameters(expression_parameters);
    }

    public DomainParticipant get_participant() {
        return contentFilteredTopic.get_participant();
    }

    public void set_participant(DomainParticipant dp) {
        contentFilteredTopic.set_participant(dp);
    }
    
    public boolean eval(Hashtable vars){
        return exp.eval(vars);
    }
    
    public String getPhenomenon(){
        return type_name;
    }
    
    public void read(DataInputStream is) throws IOException {
        contentFilteredTopic.read(is);
        name = contentFilteredTopic.get_name();
        type_name = contentFilteredTopic.get_type_name();
        
        parseFilterExpression();
    }
    
    public void write(DataOutputStream os) throws IOException {
        contentFilteredTopic.write(os);
    }
}

/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package com.dewcis.utils;

import com.dewcis.biometrics.Device;
import java.net.URISyntaxException;
import java.util.Map;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.logging.Logger;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.table.AbstractTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class tableModel extends AbstractTableModel {
	Logger log = Logger.getLogger(tableModel.class.getName());
	
	private Vector<Vector<String>> data;
	private Vector<String> titles;
	String[] userAC = null;
	String[] userIN = null;
	String[] userALL = null;
	
	public tableModel() {
		titles = new Vector<String>();
		data = new Vector<Vector<String>>();
	}
	
	public int getColumnCount() {
		return titles.size();
	}

	public int getRowCount() {
		return data.size();
	}

	public String getColumnName(int col) {
		return titles.get(col);
	}

	public String getValueAt(int row, int col) {
		return data.get(row).get(col);
	}
	
	public Vector<String> getRowValues(int row) {
		return data.get(row);
	}
	
	public Vector<String> getTitles() {
		return titles;
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public void userslist(String sessionId){
            
        try {
            Device dev = new Device();
            String results = dev.userslist(sessionId);
            
            JSONObject jResults = new JSONObject(results);
            JSONArray jresponse = (JSONArray) jResults.get("records");
            
            ArrayList<Object> listAC = new ArrayList<Object>();
            ArrayList<Object> listIN = new ArrayList<Object>();
            ArrayList<Object> listALL = new ArrayList<Object>();
            
            for(int i=0; i<jresponse.length(); i++){
                if (jresponse.getJSONObject(i).getString("status").equals("AC")) {
                    listAC.add(""+jresponse.getJSONObject(i).getString("user_id")+"");
                }else if (jresponse.getJSONObject(i).getString("status").equals("IN")) {
                    listIN.add(""+jresponse.getJSONObject(i).getString("user_id")+"");
                }
                listALL.add(""+jresponse.getJSONObject(i).getString("user_id")+"");
            }
            
            userAC = listAC.toArray(new String[0]);
            userIN = listIN.toArray(new String[0]);
            userALL	= listALL.toArray(new String[0]);
            
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(tableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
            
	}
	
	public void makeTableIN(Connection db, String mySql, Map<String, String> fields,String sessionId) {
            userslist(sessionId);
            // Add the titles
            for(String field : fields.keySet())
                titles.add(fields.get(field));
            try {
                Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = st.executeQuery(mySql);
                while(rs.next()) {
                    if(Arrays.asList(userIN).contains(rs.getString("entity_id"))){
                        Vector<String> row = new Vector<String>();
                        for(String field : fields.keySet())
                            row.add(rs.getString(field));
                        
                        data.add(row);
                    }
                }
            } catch (SQLException ex) {
                log.severe("Database connection SQL Error : " + ex);
            }
	}

	public void makeTableReg(Connection db, String mySql, Map<String, String> fields,String sessionId) {
            userslist(sessionId);
            // Add the titles
            for(String field : fields.keySet())
                titles.add(fields.get(field));
            try {
                Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = st.executeQuery(mySql);
                while(rs.next()) {
                    if(Arrays.asList(userAC).contains(rs.getString("entity_id"))){
                        Vector<String> row = new Vector<String>();
                        for(String field : fields.keySet())
                            row.add(rs.getString(field));
                        
                        data.add(row);
                    }
                }
            } catch (SQLException ex) {
                log.severe("Database connection SQL Error : " + ex);
            }
	}	

	public void makeTableNon(Connection db, String mySql, Map<String, String> fields,String sessionId) {
            
            userslist(sessionId);
            // Add the titles
            for(String field : fields.keySet())
                titles.add(fields.get(field));
            try {
                Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = st.executeQuery(mySql);
                while(rs.next()) {
                    if(Arrays.asList(userALL).contains(rs.getString("entity_id"))){
                        
                    }else{
                        Vector<String> row = new Vector<String>();
                        for(String field : fields.keySet())
                            row.add(rs.getString(field));
                        
                        
                        data.add(row);
                    }
                }
            } catch (SQLException ex) {
                log.severe("Database connection SQL Error : " + ex);
            }
	}
 
}

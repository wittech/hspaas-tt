package com.huashi.hsboss.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class OperSqlGen {
	
	class Menu {
		long id;
		String menuName;
		String parentName;
		String code;
		String ref;
		long parentId;
		String url;
		long position;
	}
	
	
	long menuSequence = 1;
	long operSequence = 1;
	long roleOperSequence = 1;
	long parentId = -1;
	long roleId = 1;
	
	List<Menu> menuList = new ArrayList<Menu>();
	List<Menu> operList = new ArrayList<Menu>();
	Set<String> codes = new HashSet<String>();
	
	
	@SuppressWarnings("unchecked")
	private void init(Element parent) {

		List<Element> elements = parent.elements();
		if(elements != null && !elements.isEmpty()) { // menu
			long position = 0;
			for(Element e : elements) {
				if(codes.contains(e.attributeValue("code"))) {
					throw new RuntimeException(e.attributeValue("name")+"--"+e.attributeValue("code") + " is exists");
				} else {
					codes.add(e.attributeValue("code"));
				}
				if(e.elements() != null && !e.elements().isEmpty()) {
					long priorParentId = parentId;
					//System.out.println("menu code=" + e.attributeValue("code") + ", parentId=" + (parentId == 0 ? null : parentId) + ", id=" + (parentId = menuSequence++));
					addMenu(e.attributeValue("code"), e.attributeValue("name"), parentId, (parentId = menuSequence++), e.attributeValue("url"), ++position);
			    	init(e);
			    	parentId = priorParentId;
				} else {
					//System.out.println("oper code=" + e.attributeValue("code") + ", parentId=" + parentId + ", id=" + (operSequence++));
					addOper(e.attributeValue("code"), e.attributeValue("ref"), e.attributeValue("name"), parentId, (operSequence++), e.getParent().attributeValue("name"));
					
				}
		    }
		} 
	}
	
	private void addMenu(String code, String name, long parentId, long id, String url, long position) {
		Menu menu = new Menu();
		menu.code = code;
		menu.menuName = name;
		menu.id = id;
		menu.url = url;
		menu.position = position;
		menu.parentId = parentId;
		
		menuList.add(menu);
	}
	
	private void addOper(String code, String ref, String name, long parentId, long id, String parentName) {
		Menu menu = new Menu();
		menu.code = code;
		menu.ref = ref;
		menu.menuName = name;
		menu.parentId = parentId;
		menu.id = id;
		menu.parentName = parentName;
		
		operList.add(menu);
	}
	
	public String genConstant(String operFile) throws Exception {
		SAXReader reader = new SAXReader();
	    Document document = reader.read(new File(operFile));
	    
	    init(document.getRootElement());
	    
		StringBuffer sb = new StringBuffer();
		
		sb.append(String.format("/**所有关联*/\npublic Map<String, String[]> OPER_CODE_MAP = new HashMap<String, String[]>();\n"));
		for(Menu e : operList) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("/**%2$s*/\npublic static final String OPER_CODE_%1$s = \"%1$s\";\n");
//			stringBuffer.append("{ \n\tOPER_CODE_MAP.put(OPER_CODE_%1$s, new String[]{\n\t\"%3$s\"\n\t});\n }\n");
			sb.append(String.format(stringBuffer.toString(), new Object[]{e.code, e.parentName + '_' + e.menuName, e.ref}));
		}
		return sb.toString();
	}

	public String genMenuConstant(String operFile) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(operFile));

		init(document.getRootElement());

		StringBuffer sb = new StringBuffer();

		for(Menu e : menuList) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("/**%2$s*/\npublic static final String MENU_CODE_%1$s = \"%1$s\";\n");
//			stringBuffer.append("{ \n\tOPER_CODE_MAP.put(OPER_CODE_%1$s, new String[]{\n\t\"%3$s\"\n\t});\n }\n");
			sb.append(String.format(stringBuffer.toString(), new Object[]{e.code, "" + e.menuName, e.ref}));
		}
		return sb.toString();
	}
	
	public String genSql(String operFile, String tablePrefix) throws Exception {
		SAXReader reader = new SAXReader();
	    Document document = reader.read(new File(operFile));
	    
	    init(document.getRootElement());
	    
		StringBuffer sqlBuffer = new StringBuffer();
		for(Menu e : menuList) {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into "+tablePrefix+"_menu (id, menu_name, menu_code, parent_id, menu_url, menu_position) values (");
			sb.append(e.id);
			sb.append(", ");
			sb.append("'" + e.menuName + "'");
			sb.append(", ");
			sb.append("'" + e.code + "'");
			sb.append(", ");
			if(e.parentId == 0) {
				sb.append("null");
			} else {
				sb.append(e.parentId);
			}
			if(e.url == null) {
				sb.append(", " + null );
			} else {
				sb.append(", '" + e.url + "'");
			}
			
			sb.append(", ");
			sb.append(e.position + ");");
			
			System.out.println(sb.toString());
			sqlBuffer.append(sb+"\r\n");
		}
		
		System.out.println();
		
		for(Menu e : operList) {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into "+tablePrefix+"_oper(id, oper_name, oper_code, menu_id) values (");
			sb.append(e.id);
			sb.append(", ");
			sb.append("'" + e.menuName + "'");
			sb.append(", ");
			sb.append("'" + e.code + "'");
			sb.append(", ");
			sb.append(e.parentId + ");");
			
			System.out.println(sb.toString());
			sqlBuffer.append(sb+"\r\n");
		}
		
		System.out.println();
		
//		for(Menu e : operList) {
//			StringBuilder sb = new StringBuilder();
//			sb.append("insert into "+tablePrefix+"_role_oper_ref(id, role_id, oper_id) values(");
//			sb.append((roleOperSequence++));
//			sb.append(", ");
//			sb.append(roleId);
//			sb.append(", ");
//			sb.append(e.id);
//			sb.append(");");
//			
//			System.out.println(sb.toString());
//			sqlBuffer.append(sb+"\r\n");
//		}
		return sqlBuffer.toString();
	}

	
	public static void main(String[] args) throws Exception {
		OperSqlGen gen = new OperSqlGen();
		String operFile = "E:\\华时\\workspace\\hspaas\\hspaas\\hsboss\\src\\main\\resources\\oper\\oper_list_20180904.xml";
//		gen.genSql(operFile,"hsboss");
//		System.out.println(gen.genMenuConstant(operFile));
		System.out.println(gen.genConstant(operFile));
	}
}

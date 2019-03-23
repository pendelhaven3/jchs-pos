package com.pj.magic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.pj.magic.util.QueriesUtil;

@Component
public class OnStartUp {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void fire() {
        createBirForm2307ReportTable();
	}

    private void createBirForm2307ReportTable() {
        String sql = "select count(1) from information_schema.tables where table_schema = 'jchs_pos' and table_name = 'bir_form_2307_report'";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        if (result == 0) {
            jdbcTemplate.update(QueriesUtil.getSql("createBirForm2307ReportTable"));
            jdbcTemplate.update("insert into SEQUENCE (NAME) values ('BIR_FORM_2307_REPORT_NO_SEQ')");
        }
    }
    
}

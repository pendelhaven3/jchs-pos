package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.pj.magic.dao.BirForm2307ReportDao;
import com.pj.magic.model.BirForm2307Report;
import com.pj.magic.model.Supplier;
import com.pj.magic.util.DbUtil;

@Component
public class BirForm2307ReportDaoImpl extends MagicDao implements BirForm2307ReportDao {

    private static final String BASE_SELECT_SQL =
            "select a.ID, REPORT_NO, FROM_DT, TO_DT, SUPPLIER_ID, MONTH1_NET_AMT, MONTH2_NET_AMT, MONTH3_NET_AMT, CREATE_DT, CREATED_BY"
            + " , b.NAME as SUPPLIER_NAME, b.ADDRESS as SUPPLIER_ADDRESS, b.TIN as SUPPLIER_TIN"
            + " from BIR_FORM_2307_REPORT a"
            + " join SUPPLIER b"
            + "   on b.ID = a.SUPPLIER_ID"
            + " where 1 = 1";
    
    private static final String REPORT_NUMBER_SEQUENCE = "BIR_FORM_2307_REPORT_NO_SEQ";
    
    private RowMapper<BirForm2307Report> rowMapper = new RowMapper<BirForm2307Report>() {

        @Override
        public BirForm2307Report mapRow(ResultSet rs, int rowNum) throws SQLException {
            BirForm2307Report report = new BirForm2307Report();
            report.setId(rs.getLong("ID"));
            report.setReportNumber(rs.getLong("REPORT_NO"));
            report.setFromDate(rs.getDate("FROM_DT"));
            report.setToDate(rs.getDate("TO_DT"));
            report.setSupplier(mapSupplier(rs));
            report.setMonth1NetAmount(rs.getBigDecimal("MONTH1_NET_AMT"));
            report.setMonth2NetAmount(rs.getBigDecimal("MONTH2_NET_AMT"));
            report.setMonth3NetAmount(rs.getBigDecimal("MONTH3_NET_AMT"));
            report.setCreateDate(rs.getTimestamp("CREATE_DT"));
            return report;
        }
        
        private Supplier mapSupplier(ResultSet rs) throws SQLException {
            Supplier supplier = new Supplier(rs.getLong("SUPPLIER_ID"), rs.getString("SUPPLIER_NAME"));
            supplier.setAddress(rs.getString("SUPPLIER_ADDRESS"));
            supplier.setTin(rs.getString("SUPPLIER_TIN"));
            return supplier;
        }
    };
    
    private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by CREATE_DT desc";

    @Override
    public List<BirForm2307Report> getAll() {
        return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
    }

    @Override
    public void save(BirForm2307Report report) {
        if (report.isNew()) {
            insert(report);
        } else {
            update(report);
        }
    }

    private static final String INSERT_SQL = 
            "insert into BIR_FORM_2307_REPORT"
            + " (REPORT_NO, FROM_DT, TO_DT, SUPPLIER_ID, MONTH1_NET_AMT, MONTH2_NET_AMT, MONTH3_NET_AMT, CREATE_DT)"
            + " values"
            + " (?, ?, ?, ?, ?, ?, ?, now())";

    private void insert(final BirForm2307Report report) {
        KeyHolder holder = new GeneratedKeyHolder();
        long reportNumber = getNextReportNumber();
        
        getJdbcTemplate().update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, reportNumber);
                ps.setDate(2,  DbUtil.toSqlDate(report.getFromDate()));
                ps.setDate(3,  DbUtil.toSqlDate(report.getToDate()));
                ps.setLong(4,  report.getSupplier().getId());
                ps.setBigDecimal(5, report.getMonth1NetAmount());
                ps.setBigDecimal(6, report.getMonth2NetAmount());
                ps.setBigDecimal(7, report.getMonth3NetAmount());
                return ps;
            }
        }, holder);
        
        report.setId(holder.getKey().longValue());
        report.setReportNumber(reportNumber);
    }

    private Long getNextReportNumber() {
        return getNextSequenceValue(REPORT_NUMBER_SEQUENCE);
    }

    private static final String UPDATE_SQL = 
            "update BIR_FORM_2307_REPORT"
            + " set FROM_DT = ?, TO_DT = ?, SUPPLIER_ID = ?, MONTH1_NET_AMT = ?, MONTH2_NET_AMT = ?, MONTH3_NET_AMT = ?,"
            + " CREATE_DT = now() where ID = ?";
    
    private void update(BirForm2307Report report) {
        getJdbcTemplate().update(UPDATE_SQL,
                report.getFromDate(),
                report.getToDate(),
                report.getSupplier().getId(),
                report.getMonth1NetAmount(),
                report.getMonth2NetAmount(),
                report.getMonth3NetAmount(),
                report.getId());
    }
    
    private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";

    @Override
    public BirForm2307Report get(Long id) {
        return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
    }

}

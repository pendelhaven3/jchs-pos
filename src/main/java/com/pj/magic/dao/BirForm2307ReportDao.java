package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BirForm2307Report;

public interface BirForm2307ReportDao {

    List<BirForm2307Report> getAll();

    void save(BirForm2307Report form2307Report);

    BirForm2307Report get(Long id);
    
}

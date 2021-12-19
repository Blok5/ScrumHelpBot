package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.model.Member;

public interface MemberService {
    Member findOrCreate(Member member);
}

package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.model.Member;
import com.example.scrumhelp.scrum.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findOrCreate(Member member) {
        Optional<Member> memberOptional = memberRepository.findById(member.getId());

        if (memberOptional.isEmpty()) {
            memberRepository.save(member);
            return member;
        }
        return memberOptional.get();
    }
}

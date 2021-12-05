package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.model.Member;
import com.example.scrumhelp.scrum.repository.MemberRepository;
import eye2web.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MemberService(MemberRepository memberRepository,
                         ModelMapper modelMapper)
    {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    public Member findOrCreate(User user) {
        Optional<Member> memberOptional = memberRepository.findById(user.getId());

        if (memberOptional.isEmpty()) {
            Member member = modelMapper.map(user, Member.class);
            memberRepository.save(member);
            return member;
        }
        return memberOptional.get();
    }
}

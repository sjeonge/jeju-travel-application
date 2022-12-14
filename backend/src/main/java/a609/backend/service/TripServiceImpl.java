package a609.backend.service;

import a609.backend.db.entity.*;
import a609.backend.db.repository.TripRepository;
import a609.backend.db.repository.UserRepository;
import a609.backend.db.repository.UserTripRepository;
import a609.backend.payload.response.FindTripDTO;
import a609.backend.payload.response.TripInfoDTO;
import a609.backend.payload.response.UserDTO;
import a609.backend.util.Algorithm;
import a609.backend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TripServiceImpl implements TripService{

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTripRepository userTripRepository;

    @Autowired
    TripScheduleService tripScheduleService;

    @Autowired
    Algorithm algorithm;

    @Override
    public FindTripDTO showTripInfo(Long tripId, String token) {
        Long kakaoId = (Long) jwtUtil.parseJwtToken(token).get("id");
        int cnt = userTripRepository.countByUserKakaoIdAndTripTripId(kakaoId, tripId);
        if(cnt ==0){
            return null;
        }
        Trip trip = tripRepository.findOneByTripId(tripId);
        FindTripDTO findTripDTO = new FindTripDTO();
        findTripDTO.setTripId(trip.getTripId());
        findTripDTO.setTripName(trip.getTripName());
        findTripDTO.setStartDate(trip.getStartDate());
        findTripDTO.setPeriodInDays(trip.getPeriodInDays());
        findTripDTO.setVehicle(trip.getVehicle());
        findTripDTO.setStyle(Integer.parseInt(Integer.toBinaryString(trip.getStyle())));
        //멤버랑
        List<UserTrip> userTrip = userTripRepository.findByTripTripId(trip.getTripId());
        List<UserDTO> user = new ArrayList<>();
        for (UserTrip userTrip1 : userTrip) {
            UserDTO userDTO = new UserDTO();
            userDTO.setKakaoId(userTrip1.getUser().getKakaoId());
            userDTO.setNickname(userTrip1.getUser().getNickname());
            userDTO.setImagePath(userTrip1.getUser().getImagePath());
            user.add(userDTO);
        }


        findTripDTO.setMember(user);
        return findTripDTO;
    }


    @Override
    public TripInfoDTO showTripList(String token) {
        List<UserTrip> userTripList = userTripRepository.findByUserKakaoId((Long)jwtUtil.parseJwtToken(token).get("id"));
        TripInfoDTO tripInfoDTO = new TripInfoDTO();
        tripInfoDTO.setUserUid((Long) jwtUtil.parseJwtToken(token).get("id"));
        List<FindTripDTO> tripList = new ArrayList<>();
        List<UserDTO> user = new ArrayList<>();
        TripInfoDTO tripInfoDTO1 = new TripInfoDTO();
        for (UserTrip userTrip : userTripList) {
            Trip trip = userTrip.getTrip();
//            TripInfoDTO tripInfoDTO1 = new TripInfoDTO();
            FindTripDTO findTripDTO = new FindTripDTO();
            findTripDTO.setTripId(trip.getTripId());
            findTripDTO.setTripName(trip.getTripName());
            findTripDTO.setStartDate(trip.getStartDate());
            findTripDTO.setPeriodInDays(trip.getPeriodInDays());
            findTripDTO.setVehicle(trip.getVehicle());
            findTripDTO.setStyle(Integer.parseInt(Integer.toBinaryString(trip.getStyle())));

            //멤버랑
            List<UserTrip> userTrip2 = userTripRepository.findByTripTripId(trip.getTripId());
            List<UserDTO> user2 = new ArrayList<>();
            for (UserTrip userTrip1 : userTrip2) {
                UserDTO userDTO = new UserDTO();
                userDTO.setKakaoId(userTrip1.getUser().getKakaoId());
                userDTO.setNickname(userTrip1.getUser().getNickname());
                userDTO.setImagePath(userTrip1.getUser().getImagePath());
                user2.add(userDTO);
            }
            findTripDTO.setMember(user2);


            tripList.add(findTripDTO);
//            tripInfoDTO.setUserUid((Long) jwtUtil.parseJwtToken(token).get("id"));
//            tripInfoDTO.setTripList(tripList);
//            tripInfoDTO=tripInfoDTO1;
            ////
             }
        tripInfoDTO.setTripList(tripList);
//        tripInfoDTO.add(tripInfoDTO1);
        return tripInfoDTO;
    }

    @Override
    public String registerTrip(Trip trip,String token) {
        User user = userRepository.findOneByKakaoId((Long)jwtUtil.parseJwtToken(token).get("id"));
        Trip trip1 = trip;
        trip.setStyle(Integer.parseInt(trip.getStyle().toString(),2));
        if(trip1.getVehicle()==null){
            trip1.setVehicle("car");
        }
        trip1.setTripName(user.getNickname()+"의 여행");
        //방장권한도 줘야됨
        Trip savedTrip = tripRepository.save(trip1);

        UserTrip userTrip = new UserTrip();
        userTrip.setTrip(savedTrip);
        userTrip.setUser(user);
        int[] visit = new int[4000];

        for(int i=0;i<savedTrip.getPeriodInDays();i++){//날짜별로 일정만들기
            tripScheduleService.registerSchedule(savedTrip,i,visit,false,0.0,0,0,0);

        }

        return userTripRepository.save(userTrip).getTrip().getTripId().toString();


    }

    @Override
    public int addUser(Long tripId, String token) {
        //1. 성공 2. 중복 3. 인원초과
        Long kakaoId = (Long)jwtUtil.parseJwtToken(token).get("id");
        int maxMember = tripRepository.findOneByTripId(tripId).getMaxMemberCnt();
        if(userTripRepository.countByTripTripId(tripId)>maxMember){
            return 3;
        }else if(userTripRepository.countByUserKakaoIdAndTripTripId(kakaoId, tripId)==0){
            UserTrip userTrip = new UserTrip();
            userTrip.setUser(userRepository.findOneByKakaoId((Long)jwtUtil.parseJwtToken(token).get("id")));
            userTrip.setTrip(tripRepository.findOneByTripId(tripId));
            userTripRepository.save(userTrip);
            return 1;
        }else{
            return 2;
        }
    }

    @Override
    @Transactional
    public void updateTrip(Long tripId,Trip trip) {
        Trip originTrip = tripRepository.findOneByTripId(tripId);
        originTrip.setTripName(trip.getTripName());
        originTrip.setStartDate(trip.getStartDate());
        originTrip.setStyle(Integer.parseInt(trip.getStyle().toString(),2));
        originTrip.setVehicle(trip.getVehicle());
    }

    @Override
    @Transactional
    public void deleteTrip(Long tripId) {
        tripRepository.deleteTripByTripId(tripId);
    }


    @Transactional
    @Override
    public void deleteUserTrip(Long tripId, String jwt) {
        Long kakaoId= (Long)jwtUtil.parseJwtToken(jwt).get("id");
        userTripRepository.deleteByTripTripIdAndUserKakaoId(tripId, kakaoId);
    }

}

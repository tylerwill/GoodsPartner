//package com.goodspartner.service.impl;
//
//import com.goodspartner.dto.DistanceMatrix;
//import com.goodspartner.dto.MapPoint;
//import com.goodspartner.service.GraphhopperService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GraphhopperServiceTest {
////    @Autowired
////    GraphhopperService graphhopperService;
//
////            [30.51589965820313, 50.39451208023374],
////            [30.492553710937504, 50.421643882044734],
////            [30.53787231445313, 50.44613667409076],
////            [30.506286621093754, 50.51080663715927],
////            [30.63949584960938, 50.40589182397226],
////            [30.492553710937504, 50.47323891440969],
////            [30.484313964843754, 50.43389186200793],
////            [30.48294067382813, 50.416393777951086],
////            [30.5775260925293, 50.48569199256476],
////            [30.68567276000977, 50.384443106343504],
////            [30.42526245117188, 50.376233121659304],
////            [30.479335784912113, 50.43421988933523],
////            [30.4951286315918, 50.45848760468376],
////            [30.516414642333988, 50.46930568174486],
////            [30.59503555297852, 50.44821360472845],
////            [30.607910156250004, 50.51222576184578],
////            [30.437793731689457, 50.42820569386265],
////            [30.555038452148438, 50.38488093227791],
////            [30.521907806396488, 50.448104294862276],
////            [30.50989151000977, 50.493664608946986]
//
//    @Test
//    void getMatrix() {
//        GraphhopperService graphhopperService = new GraphhopperService();
//
//        List<MapPoint> mapPoints = List.of(
//                new MapPoint(30.51083564758301, 50.454402863921544),
//                new MapPoint(30.51649242639542, 50.45078914537756),
//                new MapPoint(30.49261808395386, 50.43251138876327),
//                new MapPoint(30.488830804824833, 50.43443173910952),
//                new MapPoint(30.632629394531254, 50.454006666287384),
//                new MapPoint(30.48294067382813, 50.52128532157599),
//                new MapPoint(30.71640014648438, 50.4706167956567),
//                new MapPoint(30.484313964843754, 50.35860422147675),
//                new MapPoint(30.360717773437504, 50.40851753069729),
//                new MapPoint(30.51589965820313, 50.39451208023374)
//        );
//
//        DistanceMatrix matrix = graphhopperService.getMatrix(mapPoints);
//        Long[][] distance = matrix.getDistance();
//        Long[][] duration = matrix.getDuration();
//
//        System.out.println("Distance matrix");
//        for (int i = 0; i < distance.length; i++) {
//            for (int j = 0; j < distance[i].length; j++) {
//                System.out.print(distance[i][j].longValue() + " ");
//            }
//            System.out.println();
//        }
//
//
//        System.out.println("Duration matrix");
//        for (int i = 0; i < duration.length; i++) {
//            for (int j = 0; j < duration[i].length; j++) {
//                System.out.print(duration[i][j] + " ");
//            }
//            System.out.println();
//        }
//
//
//    }
//}
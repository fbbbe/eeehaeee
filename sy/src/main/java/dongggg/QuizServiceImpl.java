// 시험 로직 구현

package dongggg;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class QuizServiceImpl implements QuizService {

    private final ConceptPairRepository pairRepo = new ConceptPairRepository();

    @Override
    public List<ConceptPair> generateQuiz(List<Integer> noteIds) {
        List<ConceptPair> list = new ArrayList<>();
        if (noteIds != null) {
            for (Integer id : noteIds) {
                if (id == null) continue;
                list.addAll(ConceptPairRepository.findByNoteId(id));
            }
        }

        Collections.shuffle(list); // 랜덤 출제
        return list;
    }

    @Override
    public void updateResult(ConceptPair pair, boolean isCorrect) {
        if (pair == null || pair.getId() == 0) {
            return;
        }

        ConceptPairRepository.updateResult(pair.getId(), isCorrect);

        System.out.println("[시험 기록][저장] " + pair.getTerm() + " / " + (isCorrect ? "정답" : "오답"));
    }
}

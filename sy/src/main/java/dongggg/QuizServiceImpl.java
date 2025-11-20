// 시험 로직 구현

package dongggg;

import java.util.Collections;
import java.util.List;
import dongggg.DonggriRepository;

public class QuizServiceImpl implements QuizService {

    private final ConceptPairRepository pairRepo = new ConceptPairRepository();

    @Override
    public List<ConceptPair> generateQuiz(int noteId, int limit) {
        // 🔥 네 기존 구조에 맞게 findByNoteId로 변경
        List<ConceptPair> list = ConceptPairRepository.findByNoteId(noteId);

        Collections.shuffle(list); // 랜덤 출제
        return list.stream().limit(limit).toList();
    }

    @Override
    public void updateResult(ConceptPair pair, boolean isCorrect) {
        if (pair == null || pair.getId() == 0) {
            return;
        }

        ConceptPairRepository.updateResult(pair.getId(), isCorrect);

        int scoreDelta = isCorrect ? 10 : 0;
        int correctDelta = isCorrect ? 1 : 0;
        DonggriRepository.addProgress(scoreDelta, correctDelta);

        System.out.println("[시험 기록][저장] " + pair.getTerm() + " / " + (isCorrect ? "정답" : "오답"));
    }
}

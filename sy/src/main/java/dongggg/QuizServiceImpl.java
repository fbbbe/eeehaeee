// 시험 로직 구현

package dongggg;

import java.util.Collections;
import java.util.List;

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
        // 🔥 correctCount, wrongCount 필드는 기존 코드에 없음 → 일단 저장 없이 유지
        // 원한다면 DB 필드를 추가해줄 수 있음

        // 시험 기능 기본 버전이므로 저장 로직은 생략 또는 나중에 구현
        System.out.println("[시험 기록] " + pair.getTerm() + " / " + (isCorrect ? "정답" : "오답"));
    }
}


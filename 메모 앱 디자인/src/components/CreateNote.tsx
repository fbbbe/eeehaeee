import { ArrowLeft, FileText, BookOpen } from 'lucide-react';
import { Button } from './ui/button';

interface CreateNoteProps {
  onBack: () => void;
  onSelectGeneral: () => void;
  onSelectConcept: () => void;
}

export function CreateNote({ onBack, onSelectGeneral, onSelectConcept }: CreateNoteProps) {
  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <div className="max-w-2xl w-full">
        <div className="text-center mb-12">
          <h1 className="text-[#A855DD] mb-3">
            어떤 노트를 만들까요?
          </h1>
          <p className="text-[#D9B5FF]">
            나중에 만들어진 타입에 맞춰요.
          </p>
        </div>

        <div className="space-y-4 mb-8">
          <button 
            onClick={onSelectGeneral}
            className="w-full bg-white border-2 border-[#E8DFF5] hover:border-[#A855DD] rounded-3xl p-8 transition-all duration-300 hover:shadow-lg hover:scale-105 group"
          >
            <div className="flex items-center justify-center gap-4">
              <div className="w-16 h-16 rounded-2xl bg-[#A855DD] flex items-center justify-center group-hover:scale-110 transition-transform shadow-md">
                <FileText className="w-8 h-8 text-white" />
              </div>
              <div className="text-left">
                <div className="text-[#A855DD] mb-1">일반 노트</div>
                <div className="text-sm text-[#D9B5FF]">자유롭게 노트를 작성해요</div>
              </div>
            </div>
          </button>

          <button 
            onClick={onSelectConcept}
            className="w-full bg-white border-2 border-[#E8DFF5] hover:border-[#FF9900] rounded-3xl p-8 transition-all duration-300 hover:shadow-lg hover:scale-105 group"
          >
            <div className="flex items-center justify-center gap-4">
              <div className="w-16 h-16 rounded-2xl bg-[#FFE999] flex items-center justify-center group-hover:scale-110 transition-transform shadow-md">
                <BookOpen className="w-8 h-8 text-white" />
              </div>
              <div className="text-left">
                <div className="text-[#A855DD] mb-1">개념 노트</div>
                <div className="text-sm text-[#D9B5FF]">체계적으로 개념을 정리해요</div>
              </div>
            </div>
          </button>
        </div>

        <Button
          onClick={onBack}
          variant="outline"
          className="w-full rounded-2xl py-6 border-2 border-[#E8DFF5] hover:border-[#A855DD] text-[#A855DD] hover:bg-[#F5F0FB] transition-all"
        >
          <ArrowLeft className="w-5 h-5 mr-2" />
          되돌아가기
        </Button>
      </div>
    </div>
  );
}
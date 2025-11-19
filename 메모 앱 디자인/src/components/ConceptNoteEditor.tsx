import { useState } from 'react';
import { ArrowLeft, Plus, X } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';

interface ConceptNoteEditorProps {
  onBack: () => void;
}

interface ConceptPair {
  id: string;
  concept: string;
  description: string;
}

export function ConceptNoteEditor({ onBack }: ConceptNoteEditorProps) {
  const [title, setTitle] = useState('');
  const [currentConcept, setCurrentConcept] = useState('');
  const [currentDescription, setCurrentDescription] = useState('');
  const [concepts, setConcepts] = useState<ConceptPair[]>([]);

  const handleAddConcept = () => {
    if (currentConcept.trim() && currentDescription.trim()) {
      setConcepts([
        ...concepts,
        {
          id: Date.now().toString(),
          concept: currentConcept,
          description: currentDescription,
        },
      ]);
      setCurrentConcept('');
      setCurrentDescription('');
    }
  };

  const handleRemoveConcept = (id: string) => {
    setConcepts(concepts.filter((c) => c.id !== id));
  };

  const handleSave = () => {
    console.log('Saving concept note:', { title, concepts });
    onBack();
  };

  return (
    <div className="min-h-screen p-6">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <Button
            onClick={onBack}
            variant="ghost"
            className="mb-4 text-[#A855DD] hover:text-[#9333CC] hover:bg-[#F5F0FB] rounded-xl"
          >
            <ArrowLeft className="w-5 h-5 mr-2" />
            뒤로가기
          </Button>
        </div>

        {/* Title Input */}
        <div className="mb-6">
          <Input
            type="text"
            placeholder="개념을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="text-lg border-2 border-[#E8DFF5] focus:border-[#A855DD] rounded-2xl px-6 py-4 bg-white placeholder:text-[#C4B5D8] shadow-sm"
          />
        </div>

        {/* Add Concept Section */}
        <div className="bg-white border-2 border-[#E8DFF5] rounded-3xl p-8 mb-6 shadow-md">
          <div className="flex gap-4 mb-4">
            <div className="flex-1">
              <label className="text-[#A855DD] mb-2 block">개념 입력</label>
              <Input
                type="text"
                placeholder="Ex. 대의민주 혁명"
                value={currentConcept}
                onChange={(e) => setCurrentConcept(e.target.value)}
                className="border-2 border-[#E8DFF5] focus:border-[#A855DD] rounded-2xl px-6 py-3 bg-[#F8F7FF] placeholder:text-[#C4B5D8]"
              />
            </div>
            <div className="flex-1">
              <label className="text-[#A855DD] mb-2 block">설명 입력</label>
              <Input
                type="text"
                placeholder="설명을 입력하세요"
                value={currentDescription}
                onChange={(e) => setCurrentDescription(e.target.value)}
                className="border-2 border-[#E8DFF5] focus:border-[#A855DD] rounded-2xl px-6 py-3 bg-[#F8F7FF] placeholder:text-[#C4B5D8]"
              />
            </div>
          </div>

          {/* Add Button */}
          <div className="flex justify-center">
            <Button
              onClick={handleAddConcept}
              className="bg-[#FFE999] hover:bg-[#FFD966] text-white rounded-full w-12 h-12 p-0 shadow-md hover:shadow-lg transition-all"
            >
              <Plus className="w-6 h-6" />
            </Button>
          </div>
        </div>

        {/* Concepts List */}
        {concepts.length > 0 && (
          <div className="bg-white border-2 border-[#E8DFF5] rounded-3xl p-8 mb-6 shadow-md">
            <h3 className="text-[#A855DD] mb-4">추가된 개념</h3>
            <div className="space-y-3">
              {concepts.map((concept) => (
                <div
                  key={concept.id}
                  className="flex items-start gap-4 p-4 bg-[#FFF9E6] border border-[#FFE999] rounded-2xl group hover:bg-[#FFF4CC] transition-colors"
                >
                  <div className="flex-1">
                    <div className="text-[#A855DD] mb-1">{concept.concept}</div>
                    <div className="text-sm text-[#D9B5FF]">{concept.description}</div>
                  </div>
                  <button
                    onClick={() => handleRemoveConcept(concept.id)}
                    className="text-[#D9B5FF] hover:text-red-500 transition-colors opacity-0 group-hover:opacity-100"
                  >
                    <X className="w-5 h-5" />
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex justify-end gap-3">
          <Button
            onClick={onBack}
            variant="outline"
            className="border-2 border-[#E8DFF5] text-[#A855DD] hover:bg-[#F5F0FB] hover:border-[#A855DD] rounded-2xl px-8 py-6 transition-all"
          >
            닫기
          </Button>
          <Button
            onClick={handleSave}
            className="bg-[#FFE999] hover:bg-[#FFD966] text-white rounded-2xl px-8 py-6 shadow-md hover:shadow-lg transition-all"
          >
            저장
          </Button>
        </div>
      </div>
    </div>
  );
}
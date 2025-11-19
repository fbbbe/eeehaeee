import { useState } from 'react';
import { ArrowLeft, Trash2 } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';

interface GeneralNoteEditorProps {
  onBack: () => void;
}

export function GeneralNoteEditor({ onBack }: GeneralNoteEditorProps) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const handleSave = () => {
    // Save logic here
    console.log('Saving note:', { title, content });
    onBack();
  };

  const handleDelete = () => {
    if (confirm('정말 삭제하시겠습니까?')) {
      onBack();
    }
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
          
          <h1 className="text-[#A855DD] mb-2">노트 편집</h1>
          <p className="text-[#D9B5FF]">새 노트</p>
        </div>

        {/* Editor Card */}
        <div className="bg-white border-2 border-[#E8DFF5] rounded-3xl p-8 mb-6 shadow-md">
          <div className="space-y-6">
            {/* Title Input */}
            <div>
              <Input
                type="text"
                placeholder="제목을 입력하세요"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="text-lg border-2 border-[#E8DFF5] focus:border-[#A855DD] rounded-2xl px-6 py-4 bg-[#FFF5F7] placeholder:text-[#C4B5D8]"
              />
            </div>

            {/* Content Textarea */}
            <div>
              <Textarea
                placeholder="내용을 입력하세요"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows={15}
                className="border-2 border-[#E8DFF5] focus:border-[#A855DD] rounded-2xl px-6 py-4 bg-[#F8F7FF] placeholder:text-[#C4B5D8] resize-none"
              />
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center justify-between gap-4">
          <Button
            onClick={handleDelete}
            variant="outline"
            className="border-2 border-red-200 text-red-600 hover:bg-red-50 hover:border-red-300 rounded-2xl px-8 py-6 transition-all"
          >
            <Trash2 className="w-5 h-5 mr-2" />
            삭제
          </Button>

          <div className="flex gap-3">
            <Button
              onClick={onBack}
              variant="outline"
              className="border-2 border-[#E8DFF5] text-[#A855DD] hover:bg-[#F5F0FB] hover:border-[#A855DD] rounded-2xl px-8 py-6 transition-all"
            >
              취소
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
    </div>
  );
}